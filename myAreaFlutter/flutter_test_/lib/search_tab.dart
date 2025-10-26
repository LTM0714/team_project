import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'dart:convert';
import 'package:flutter/services.dart';

Map<String, LatLng> regionCoordinates = {};

final LatLngBounds koreaBounds = LatLngBounds(
  const LatLng(33.0, 124.0),
  const LatLng(43.0, 132.0),
);

class SearchTab extends StatefulWidget {
  final List<String> regions; 
  final List<String> interestRegions;
  final void Function(List<String>) onEditInterestRegions;

  const SearchTab({
    required this.regions,
    required this.interestRegions,
    required this.onEditInterestRegions,
    super.key,
  });

  @override
  State<SearchTab> createState() => _SearchTabState();
}

class _SearchTabState extends State<SearchTab> with TickerProviderStateMixin {
  final TextEditingController _searchController = TextEditingController();
  List<String> _filteredRegions = [];
  String _searchText = '';
  late MapController _mapController;
  LatLng _mapCenter = LatLng(36.5, 127.8);
  double _mapZoom = 7.0;
  LatLng? _selectedMarker;
  
  bool _isLoadingCoords = true;

  final Map<String, LatLng> _majorCityCoords = {
    '부산': LatLng(35.1796, 129.0756),
    '대구': LatLng(35.8722, 128.6014),
    '인천': LatLng(37.4563, 126.7052),
    '광주': LatLng(35.1595, 126.8526),
    '대전': LatLng(36.3504, 127.3845),
    '울산': LatLng(35.5384, 129.3113),
    '세종': LatLng(36.4800, 127.2890)
  };
  
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

  @override
  void initState() {
    super.initState();
    _mapController = MapController();
    _filteredRegions = widget.regions;
    _searchController.addListener(_onSearchTextChanged);
    
    _loadRegionCoordinates().then((_) {
      WidgetsBinding.instance.addPostFrameCallback((_) => _fitInterestRegionBounds());
    });
  }

  Future<void> _loadRegionCoordinates() async {
    try {
      final String response = await rootBundle.loadString('assets/seoul_gu_coords.json');
      final List<dynamic> jsonList = jsonDecode(response);

      final Map<String, LatLng> loadedCoords = {};

      for (var item in jsonList) {
        final name = item['address'].toString(); 
        loadedCoords[name] = LatLng(
          (item['latitude'] as num).toDouble(),
          (item['longitude'] as num).toDouble(),
        );
      }
      
      _majorCityCoords.forEach((name, coords) {
          loadedCoords[name] = coords;
      });

      regionCoordinates = loadedCoords;

      setState(() {
        _isLoadingCoords = false;
      });

    } catch (e) {
      print("지역 좌표 로딩 오류: $e");
      setState(() {
        _isLoadingCoords = false;
      });
    }
  }

  @override
  void didUpdateWidget(SearchTab oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.interestRegions != widget.interestRegions && !_isLoadingCoords) {
      _fitInterestRegionBounds();
    }
  }

  @override
  void dispose() {
    _searchController.removeListener(_onSearchTextChanged);
    _searchController.dispose();
    super.dispose();
  }

  void _onSearchTextChanged() {
    setState(() {
      _searchText = _searchController.text.trim();
      
      _filteredRegions = widget.regions
          .where((region) {
            final displayName = _getDisplayName(region);
            return region.contains(_searchText) || displayName.contains(_searchText);
          })
          .toList();
    });
  }

  LatLng _clampToBounds(LatLng coord, LatLngBounds bounds) {
    final south = bounds.south;
    final west = bounds.west;
    final north = bounds.north;
    final east = bounds.east;
    final lat = coord.latitude.clamp(south, north);
    final lng = coord.longitude.clamp(west, east);
    return LatLng(lat, lng);
  }

  void _fitInterestRegionBounds() {
    if (_isLoadingCoords) return;
    
    final coords = widget.interestRegions
        .where((r) => regionCoordinates.containsKey(r))
        .map((r) => regionCoordinates[r]!)
        .toList();

    if (coords.isEmpty) {
      _moveMap(_clampToBounds(LatLng(36.5, 127.8), koreaBounds), 7.0);
      return;
    }
    if (coords.length == 1) {
      _moveMap(_clampToBounds(coords.first, koreaBounds), 12.0);
      return;
    }

    double minLat = coords.first.latitude;
    double maxLat = coords.first.latitude;
    double minLng = coords.first.longitude;
    double maxLng = coords.first.longitude;

    for (var c in coords) {
      if (c.latitude < minLat) minLat = c.latitude;
      if (c.latitude > maxLat) maxLat = c.latitude;
      if (c.longitude < minLng) minLng = c.longitude;
      if (c.longitude > maxLng) maxLng = c.longitude;
    }

    final bounds = LatLngBounds(
      LatLng(minLat, minLng),
      LatLng(maxLat, maxLng),
    );

    _mapController.fitCamera(
      CameraFit.bounds(
        bounds: bounds,
        padding: const EdgeInsets.all(50),
      ),
    );

    setState(() {
      _mapCenter = _clampToBounds(LatLng((minLat + maxLat) / 2, (minLng + maxLng) / 2), koreaBounds);
    });
  }

  void _moveMap(LatLng latLng, double zoom) {
    final clamped = _clampToBounds(latLng, koreaBounds);
    final clampedZoom = zoom.clamp(6.0, 18.0);
    _mapController.move(clamped, clampedZoom);
    setState(() {
      _mapCenter = clamped;
      _mapZoom = clampedZoom;
      _selectedMarker = clamped;
    });
  }

  void _onRegionSearched(String region) {
    if (_isLoadingCoords) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('지역 데이터를 로드 중입니다. 잠시 후 다시 시도해주세요.')),
      );
      return;
    }
    
    final coord = regionCoordinates[region];
    if (coord != null) {
      _moveMap(coord, 13.0);
      _showAddInterestDialog(region, coord);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('좌표 데이터가 없는 지역입니다.')),
      );
    }
  }

  void _showAddInterestDialog(String fullRegionName, LatLng coord) async {
    final displayName = _getDisplayName(fullRegionName);
    
    final result = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('관심지역 추가'),
        content: Text('$displayName을(를) 관심지역에 추가하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('추가'),
          ),
        ],
      ),
    );
    if (result == true && !widget.interestRegions.contains(fullRegionName)) {
      final newRegions = List<String>.from(widget.interestRegions)..add(fullRegionName);
      widget.onEditInterestRegions(newRegions);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('$displayName이(가) 관심지역에 추가되었습니다.')),
      );
    }
  }

  void _onMapMarkerTapped(String region, LatLng latLng) {
    _showAddInterestDialog(region, latLng);
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoadingCoords) {
      return const Center(child: CircularProgressIndicator());
    }
    
    final interestMarkers = widget.interestRegions
        .where((r) => regionCoordinates.containsKey(r))
        .map((r) => Marker(
              width: 44,
              height: 44,
              point: regionCoordinates[r]!,
              child: GestureDetector(
                onTap: () => _onMapMarkerTapped(r, regionCoordinates[r]!),
                child: const Icon(Icons.place, color: Colors.green, size: 38),
              ),
            ))
        .toList();

    final searchedMarker = (_selectedMarker != null)
        ? [
            Marker(
              width: 48,
              height: 48,
              point: _selectedMarker!,
              child: const Icon(Icons.location_on, color: Colors.red, size: 42),
            )
          ]
        : [];

    return Scaffold(
      appBar: AppBar(
        title: const Text('지역 검색'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 20, 16, 10),
            child: Row(
              children: [
                Icon(Icons.search, size: 28, color: Colors.grey[600]),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      hintText: '지역명을 입력하세요 (예: 강남, 부산, 세종)',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    ),
                    onSubmitted: (value) {
                      _onRegionSearched(value); 
                    },
                  ),
                ),
              ],
            ),
          ),
          if (_searchText.isNotEmpty && _filteredRegions.isNotEmpty)
            Container(
              margin: const EdgeInsets.symmetric(horizontal: 16),
              padding: const EdgeInsets.symmetric(vertical: 4),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(10),
                boxShadow: const [BoxShadow(color: Colors.black12, blurRadius: 4)],
              ),
              constraints: const BoxConstraints(maxHeight: 240),
              child: ListView(
                shrinkWrap: true,
                children: _filteredRegions.map((region) {
                  final displayName = _getDisplayName(region);
                  
                  return ListTile(
                    title: Text(displayName),
                    onTap: () {
                      _searchController.text = displayName; 
                      
                      _onRegionSearched(region);
                    },
                  );
                }).toList(),
              ),
            ),
          const SizedBox(height: 12),
          Expanded(
            child: Container(
              margin: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(18),
                border: Border.all(color: Colors.blueGrey, width: 1),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(18),
                child: FlutterMap(
                  mapController: _mapController,
                  options: MapOptions(
                    center: _clampToBounds(_mapCenter, koreaBounds),
                    zoom: _mapZoom.clamp(6.0, 18.0),
                    minZoom: 6,
                    maxZoom: 18,
                    interactionOptions: const InteractionOptions(
                      flags: InteractiveFlag.pinchZoom | InteractiveFlag.drag,
                    ),
                    cameraConstraint: CameraConstraint.contain(bounds: koreaBounds),
                  ),
                  children: [
                    TileLayer(
                      urlTemplate: 'https://a.tile.openstreetmap.de/{z}/{x}/{y}.png',
                      userAgentPackageName: 'com.example.yourapp',
                    ),
                    MarkerLayer(markers: [...interestMarkers, ...searchedMarker]),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
