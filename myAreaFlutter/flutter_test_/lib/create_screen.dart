import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:exif/exif.dart';
import 'dart:io';
import 'dart:math';
import 'dart:convert';
import 'package:flutter/services.dart';

import 'post_model.dart';

class CreateScreen extends StatefulWidget {
  final Function(Post) onUpload;
  final List<String> regions;
  
  const CreateScreen({required this.onUpload, required this.regions, super.key});

  @override
  State<CreateScreen> createState() => _CreateScreenState();
}

class _CreateScreenState extends State<CreateScreen> {
  File? _imageFile;
  String? _selectedRegion;
  final TextEditingController _searchController = TextEditingController(); 
  final TextEditingController _descController = TextEditingController();
  
  Map<String, List<double>> _regionCoords = {}; 
  bool _isLoadingRegions = true; 

  final Map<String, List<double>> _majorCityCoords = {
    '부산': [35.1796, 129.0756], 
    '대구': [35.8722, 128.6014], 
    '인천': [37.4563, 126.7052], 
    '광주': [35.1595, 126.8526], 
    '대전': [36.3504, 127.3845], 
    '울산': [35.5384, 129.3113], 
    '세종': [36.4800, 127.2890]
  };
  
  final List<double> defaultCoords = [37.5665, 126.9780]; 

  @override
  void initState() {
    super.initState();
    _loadRegionData(); 
    _searchController.addListener(_onSearchChanged);
  }
  
  void _onSearchChanged() {
    setState(() {
    });
  }

  @override
  void dispose() {
    _searchController.removeListener(_onSearchChanged);
    _searchController.dispose();
    _descController.dispose();
    super.dispose();
  }

  String _getDisplayName(String fullRegionName) {
    if (fullRegionName.endsWith('구')) {
      String guName = fullRegionName; 

      String simplified = guName.endsWith('구') ? guName.substring(0, guName.length - 1) : guName;
      simplified = simplified.trim(); 

      if (simplified.length == 1 && guName.length > 1) { 
        return guName; 
      }
      
      return simplified; 
    }
    else if (fullRegionName.endsWith('광역시') || fullRegionName.endsWith('특별자치시')) {
      return fullRegionName.replaceAll('광역시', '').replaceAll('특별자치시', '').trim();
    }
    else if (fullRegionName == '지역 정보 없음') {
      return fullRegionName;
    }
    return fullRegionName;
  }
  
  Future<void> _loadRegionData() async {
    try {
      final String response = await rootBundle.loadString('assets/seoul_gu_coords.json');
      final List<dynamic> jsonList = jsonDecode(response);

      final Map<String, List<double>> loadedCoords = {};
      final List<String> loadedRegionNames = ['지역 정보 없음']; 
      
      for (var item in jsonList) {
        final name = item['address'].toString(); 
        loadedCoords[name] = [
          (item['latitude'] as num).toDouble(),
          (item['longitude'] as num).toDouble(),
        ];
        loadedRegionNames.add(name);
      }
      
      _majorCityCoords.forEach((name, coords) {
        if (!loadedCoords.containsKey(name)) { 
           loadedCoords[name] = coords;
           loadedRegionNames.add(name);
        }
      });
      
      setState(() {
        _regionCoords = loadedCoords;
        _isLoadingRegions = false;
        widget.regions.clear();
        loadedRegionNames.sort(); 
        widget.regions.addAll(loadedRegionNames);
      });

    } catch (e) {
      print("지역 데이터 로딩 오류: $e");
      setState(() {
        _isLoadingRegions = false;
      });
    }
  }

  double _calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    const R = 6371; 
    
    final dLat = _degreesToRadians(lat2 - lat1);
    final dLon = _degreesToRadians(lon2 - lon1);
    final latM = _degreesToRadians((lat1 + lat2) / 2);
    
    final x = dLon * cos(latM);
    final distance = R * sqrt(x * x + dLat * dLat); 
    
    return distance; 
  }

  double _degreesToRadians(double degrees) {
    return degrees * pi / 180;
  }

  Future<void> _pickImage(ImageSource source) async {
    if (_isLoadingRegions) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('지역 데이터를 로드 중입니다. 잠시 후 다시 시도해주세요.')),
      );
      return;
    }
    
    final pickedFile = await ImagePicker().pickImage(source: source);
    if (pickedFile == null) return;

    final file = File(pickedFile.path);
    setState(() => _imageFile = file);

    await _extractPhotoLocation(file);
  }

  Future<void> _extractPhotoLocation(File imageFile) async {
    final String noLocationRegion = '지역 정보 없음'; 
    final String fallbackRegion = '구로구'; 
    
    try {
      final bytes = await imageFile.readAsBytes();
      final tags = await readExifFromBytes(bytes);
      
      final latTag = tags['GPS GPSLatitude'];
      final lonTag = tags['GPS GPSLongitude'];

      if (latTag == null || lonTag == null || latTag.values.toList().isEmpty || lonTag.values.toList().isEmpty) {
        
        setState(() => _selectedRegion = noLocationRegion);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('사진에 위치 정보가 없습니다. 지역을 직접 선택해주세요.')),
        );
        return;
      }

      final latRef = tags['GPS GPSLatitudeRef']?.printable ?? 'N';
      final lonRef = tags['GPS GPSLongitudeRef']?.printable ?? 'E';

      final List<dynamic> latValues = latTag.values.toList();
      final List<dynamic> lonValues = lonTag.values.toList();

      double lat = (latValues[0].toDouble() +
          latValues[1].toDouble() / 60 +
          latValues[2].toDouble() / 3600) *
          (latRef == 'S' ? -1 : 1);
      double lon = (lonValues[0].toDouble() +
          lonValues[1].toDouble() / 60 +
          lonValues[2].toDouble() / 3600) *
          (lonRef == 'W' ? -1 : 1);


      String? closestRegion;
      double minDistance = double.infinity;

      if (_regionCoords.isNotEmpty) { 
          _regionCoords.forEach((regionName, coords) {
              if (regionName == noLocationRegion) return; 
              
              final regionLat = coords[0];
              final regionLon = coords[1];

              final distance = _calculateDistance(lat, lon, regionLat, regionLon);

              if (distance < minDistance) {
                  minDistance = distance;
                  closestRegion = regionName;
              }
          });
      }

      if (closestRegion != null) {
          setState(() => _selectedRegion = closestRegion);

          ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                  content: Text(
                      '사진 위치 자동 인식: ${_getDisplayName(closestRegion!)} (${minDistance.toStringAsFixed(1)}km 거리)')),
          );
      } else { 
          setState(() => _selectedRegion = fallbackRegion);
          
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('${_getDisplayName(fallbackRegion)}이(가) 선택되었습니다.')),
          );
      }

    } catch (e) {
      print('위치 정보 추출 오류: $e');
      setState(() => _selectedRegion = noLocationRegion);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('위치 정보를 읽는 중 오류가 발생했습니다. 지역을 직접 선택해주세요.')),
      );
    }
  }

  void _upload() {
    if (_imageFile == null || _selectedRegion == null || _selectedRegion == '지역 정보 없음' || _descController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('사진, 지역, 설명을 모두 입력해주세요.')),
      );
      return;
    }

    final coords = _regionCoords[_selectedRegion] ?? defaultCoords;

    final post = Post(
      image: _imageFile!,
      region: _selectedRegion!, 
      desc: _descController.text,
      date: DateTime.now(),
      latitude: coords[0],
      longitude: coords[1],
    );

    widget.onUpload(post);

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('업로드 완료!')),
    );

    setState(() {
      _imageFile = null;
      _selectedRegion = null;
      _searchController.clear(); 
      _descController.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingRegions) {
      return Scaffold(
        appBar: AppBar(title: const Text('사진 업로드')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [ 
              const CircularProgressIndicator(), 
              const SizedBox(height: 16),
              const Text('지역 데이터 로드 중...')
            ],
          ),
        ),
      );
    }
    
    final currentQuery = _searchController.text.trim();
    
    final filteredRegions = widget.regions.where((r) {
      final displayName = _getDisplayName(r);
      return r.contains(currentQuery) || displayName.contains(currentQuery);
    }).toList();

    String selectedRegionDisplayName = _selectedRegion != null ? _getDisplayName(_selectedRegion!) : '';
    
    return Scaffold(
      appBar: AppBar(title: const Text('사진 업로드')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            GestureDetector(
              onTap: () => _pickImage(ImageSource.gallery),
              child: Container(
                height: 200,
                margin: const EdgeInsets.only(bottom: 16),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(16),
                  color: Colors.grey[200],
                  border: Border.all(color: Colors.grey.shade400),
                ),
                child: _imageFile != null
                    ? ClipRRect(
                        borderRadius: BorderRadius.circular(16),
                        child: Image.file(
                          _imageFile!,
                          fit: BoxFit.cover,
                          width: double.infinity,
                          height: double.infinity,
                        ),
                      )
                    : Center(
                        child: Icon(
                          Icons.photo_library_rounded,
                          color: Colors.grey[500],
                          size: 100,
                        ),
                      ),
              ),
            ),

            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _pickImage(ImageSource.camera),
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20)),
                      minimumSize: const Size(0, 56),
                    ),
                    child: const Text('사진 찍기', style: TextStyle(fontSize: 18)),
                  ),
                ),
                const SizedBox(width: 20),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => _pickImage(ImageSource.gallery),
                    style: ElevatedButton.styleFrom(
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(20)),
                      minimumSize: const Size(0, 56),
                    ),
                    child:
                        const Text('직접 선택', style: TextStyle(fontSize: 18)), 
                  ),
                ),
              ],
            ),

            const SizedBox(height: 24),
            const Text('지역',
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),

            TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: currentQuery.isEmpty && _selectedRegion != null ? '선택된 지역: $selectedRegionDisplayName' : '지역 검색 (예: 강남, 부산, 중구)',
                prefixIcon: const Icon(Icons.search),
                border:
                    OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                contentPadding:
                    const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
              ),
              onChanged: (_) {}, 
            ),

            if (currentQuery.isNotEmpty)
              Container(
                constraints: const BoxConstraints(maxHeight: 150),
                margin: const EdgeInsets.only(top: 8),
                decoration: BoxDecoration(
                  color: Colors.white,
                  border: Border.all(color: Colors.grey.shade300),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: ListView.builder(
                  shrinkWrap: true,
                  itemCount: filteredRegions.length,
                  itemBuilder: (context, index) {
                    final fullRegionName = filteredRegions[index];
                    final displayName = _getDisplayName(fullRegionName); 
                    return ListTile(
                      title: Text(displayName),
                      onTap: () {
                        setState(() {
                          _selectedRegion = fullRegionName;
                          _searchController.clear(); 
                        });
                      },
                    );
                  },
                ),
              ),

            const SizedBox(height: 12),

            if (_selectedRegion != null && currentQuery.isEmpty)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: Row(
                  children: [
                    Icon(
                      _selectedRegion == '지역 정보 없음' ? Icons.warning : Icons.location_on, 
                      color: _selectedRegion == '지역 정보 없음' ? Colors.orange : Colors.blueGrey, 
                      size: 20
                    ),
                    const SizedBox(width: 8),
                    Text(
                      '선택된 지역: ${selectedRegionDisplayName}',
                      style: const TextStyle(fontSize: 16, color: Colors.blueGrey),
                    ),
                    const Spacer(),
                    TextButton(
                      onPressed: () {
                        setState(() {
                          _selectedRegion = null;
                        });
                      },
                      child: const Text('초기화'),
                    )
                  ],
                ),
              ),

            const SizedBox(height: 24),
            const Text('설명',
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 8),
            TextField(
              controller: _descController,
              minLines: 3,
              maxLines: 5,
              decoration: InputDecoration(
                hintText: '이 장소에 대해 설명해주세요',
                border:
                    OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
              ),
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: _upload,
              style: ElevatedButton.styleFrom(
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20)),
                minimumSize: const Size(0, 56),
              ),
              child: const Text('업로드하기', style: TextStyle(fontSize: 18)),
            ),
          ],
        ),
      ),
    );
  }
}
