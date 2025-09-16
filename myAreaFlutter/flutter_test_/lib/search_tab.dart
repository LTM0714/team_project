import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';

const Map<String, LatLng> regionCoordinates = {
  '서울': LatLng(37.5665, 126.9780),
  '부산': LatLng(35.1796, 129.0756),
  '대구': LatLng(35.8722, 128.6025),
  '인천': LatLng(37.4563, 126.7052),
  '광주': LatLng(35.1595, 126.8526),
  '대전': LatLng(36.3504, 127.3845),
  '울산': LatLng(35.5396, 129.3114),
  // 실제 사용시 더 많이 추가
};

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
  LatLng _mapCenter = LatLng(36.5, 127.8); // 대한민국 중심 대략값
  double _mapZoom = 7.0;
  LatLng? _selectedMarker;

  @override
  void initState() {
    super.initState();
    _mapController = MapController();
    _filteredRegions = widget.regions;
    _searchController.addListener(_onSearchTextChanged);
    WidgetsBinding.instance.addPostFrameCallback((_) => _fitInterestRegionBounds());
  }

  @override
  void didUpdateWidget(SearchTab oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.interestRegions != widget.interestRegions) {
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
          .where((region) => region.contains(_searchText))
          .toList();
    });
  }

  // 관심지역들의 영역에 맞춰서 카메라 이동/확대
  void _fitInterestRegionBounds() {
    final coords = widget.interestRegions
        .where((r) => regionCoordinates.containsKey(r))
        .map((r) => regionCoordinates[r]!)
        .toList();
    if (coords.isEmpty) {
      // 아무 관심지역 없으면 전국 중심, 낮은 줌
      _moveMap(LatLng(36.5, 127.8), 7.0);
      return;
    }
    if (coords.length == 1) {
      _moveMap(coords.first, 12.0);
      return;
    }
    // 여러 개일 때 모두 포함하는 bounds 계산
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
    _mapController.fitBounds(bounds, options: FitBoundsOptions(padding: EdgeInsets.all(50)));
    // 지도 센터값 업데이트
    setState(() {
      _mapCenter = LatLng((minLat + maxLat) / 2, (minLng + maxLng) / 2);
    });
  }

  // 지도 이동 및 애니메이션
  void _moveMap(LatLng latLng, double zoom) {
    _mapController.move(latLng, zoom);
    setState(() {
      _mapCenter = latLng;
      _mapZoom = zoom;
      _selectedMarker = latLng;
    });
  }

  void _onRegionSearched(String region) {
    final coord = regionCoordinates[region];
    if (coord != null) {
      _moveMap(coord, 13.0);
      _showAddInterestDialog(region, coord);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('좌표 데이터가 없는 지역입니다.')),
      );
    }
  }

  void _showAddInterestDialog(String region, LatLng coord) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('관심지역 추가'),
        content: Text('$region을(를) 관심지역에 추가하시겠습니까?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text('취소'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: Text('추가'),
          ),
        ],
      ),
    );
    if (result == true && !widget.interestRegions.contains(region)) {
      final newRegions = List<String>.from(widget.interestRegions)..add(region);
      widget.onEditInterestRegions(newRegions);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('$region이(가) 관심지역에 추가되었습니다.')),
      );
    }
  }

  void _onMapMarkerTapped(String region, LatLng latLng) {
    _showAddInterestDialog(region, latLng);
  }

  void _addInterestRegion(String region) async {
    if (widget.interestRegions.contains(region)) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('이미 관심지역에 추가된 지역입니다.')),
      );
      return;
    }
    final coord = regionCoordinates[region];
    if (coord != null) {
      _showAddInterestDialog(region, coord);
    }
  }

  @override
  Widget build(BuildContext context) {
    // 관심지역 마커
    final interestMarkers = widget.interestRegions
        .where((r) => regionCoordinates.containsKey(r))
        .map((r) => Marker(
              width: 44,
              height: 44,
              point: regionCoordinates[r]!,
              child: GestureDetector(
                onTap: () => _onMapMarkerTapped(r, regionCoordinates[r]!),
                child: Icon(Icons.place, color: Colors.green, size: 38),
              ),
            ))
        .toList();

    // 검색 결과 마커(빨간색)
    final searchedMarker = (_selectedMarker != null)
        ? [
            Marker(
              width: 48,
              height: 48,
              point: _selectedMarker!,
              child: Icon(Icons.location_on, color: Colors.red, size: 42),
            )
          ]
        : [];

    return Scaffold(
      appBar: AppBar(
        title: Text('지역 검색'),
        centerTitle: true,
      ),
      body: Column(
        children: [
          Padding(
            padding: EdgeInsets.fromLTRB(16, 20, 16, 10),
            child: Row(
              children: [ 
                Icon(Icons.search, size: 28, color: Colors.grey[600]),
                SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      hintText: '지역명을 입력하세요',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    ),
                    onSubmitted: (value) {
                      if (regionCoordinates.containsKey(value)) {
                        _onRegionSearched(value);
                      } else {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('알 수 없는 지역입니다.')),
                        );
                      }
                    },
                  ),
                ),
              ],
            ),
          ),
          if (_searchText.isNotEmpty && _filteredRegions.isNotEmpty)
            Container(
              margin: EdgeInsets.symmetric(horizontal: 16),
              padding: EdgeInsets.symmetric(vertical: 4),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(10),
                boxShadow: [BoxShadow(color: Colors.black12, blurRadius: 4)],
              ),
              constraints: BoxConstraints(
                maxHeight: 240,
              ),
              child: ListView(
                shrinkWrap: true,
                children: _filteredRegions.map((region) {
                  return ListTile(
                    title: Text(region),
                    onTap: () {
                      _searchController.text = region;
                      _onRegionSearched(region);
                    },
                  );
                }).toList(),
              ),
            ),
          SizedBox(height: 12),
          Expanded(
            child: Container(
              margin: EdgeInsets.all(10),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(18),
                border: Border.all(color: Colors.blueGrey, width: 1),
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(18),
                child: FlutterMap(
                  mapController: _mapController,
                  options: MapOptions(
                    center: _mapCenter,
                    zoom: _mapZoom,
                    onTap: (tapPosition, latLng) {
                      setState(() {
                        _selectedMarker = latLng;
                        _mapCenter = latLng;
                      });
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('지도 클릭: ${latLng.latitude}, ${latLng.longitude}')),
                      );
                    },
                  ),
                  children: [
                    TileLayer(
                      urlTemplate: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                      subdomains: const ['a', 'b', 'c'],
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
