import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';

import 'post_model.dart';
import 'post_detail_screen.dart';

class ActivityScreen extends StatelessWidget {
  final List<Post> posts;

  const ActivityScreen({super.key, required this.posts});

  // 한반도 대략 경계 (필요 시 조정)
  static final LatLng _koreaSouthWest = LatLng(33.0, 124.0);
  static final LatLng _koreaNorthEast = LatLng(43.0, 132.0);
  static final LatLngBounds _koreaBounds = LatLngBounds(_koreaSouthWest, _koreaNorthEast);

  // 좌표를 bounds 내부로 클램프
  LatLng _clampToBounds(LatLng coord, LatLngBounds bounds) {
    final south = bounds.south;
    final west = bounds.west;
    final north = bounds.north;
    final east = bounds.east;

    final lat = coord.latitude.clamp(south, north);
    final lng = coord.longitude.clamp(west, east);
    return LatLng(lat, lng);
  }

  @override
  Widget build(BuildContext context) {
    // 초기 중심: posts.last 또는 서울 시청 기준, 그리고 bounds 내로 클램프
    final LatLng initial = posts.isNotEmpty
        ? _clampToBounds(LatLng(posts.last.latitude, posts.last.longitude), _koreaBounds)
        : const LatLng(37.5665, 126.9780);

    return Scaffold(
      appBar: AppBar(title: const Text('지도에서 보기')),
      body: FlutterMap(
        options: MapOptions(
          // 초기 카메라 중심(범위 내로 보정)
          initialCenter: initial,
          initialZoom: 13,
          // 줌 범위 제한 (필요시 조정)
          minZoom: 6,
          maxZoom: 18,
          // 회전·기울기 방지, 줌과 드래그만 허용
          interactionOptions: const InteractionOptions(
            flags: InteractiveFlag.pinchZoom | InteractiveFlag.drag,
          ),
          // cameraConstraint로 한반도 밖으로 못 나가게 제한
          cameraConstraint: CameraConstraint.contain(bounds: _koreaBounds),
        ),
        children: [
          // 오픈스트리트맵 타일 레이어 (테스트용 미러 서버)
          TileLayer(
            urlTemplate: 'https://a.tile.openstreetmap.de/{z}/{x}/{y}.png',
            userAgentPackageName: 'com.example.app',
          ),
          // 게시글들을 지도 위 마커로 표시
          MarkerLayer(
            markers: posts.asMap().entries.map((entry) {
              final post = entry.value;

              return Marker(
                point: LatLng(post.latitude, post.longitude),
                width: 60,
                height: 60,
                child: GestureDetector(
                  onTap: () {
                    // 같은 지역 게시글만 보이도록 필터링, 최신순 정렬
                    final sameRegionPosts = posts
                        .where((p) => p.region == post.region)
                        .toList()
                      ..sort((a, b) => b.date.compareTo(a.date));

                    final localIndex = sameRegionPosts.indexOf(post);

                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) => PostDetailScreen(
                          posts: sameRegionPosts,
                          initialIndex: localIndex,
                        ),
                      ),
                    );
                  },
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(8),
                    child: Image.file(
                      post.image,
                      fit: BoxFit.cover,
                      width: 60,
                      height: 60,
                    ),
                  ),
                ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }
}
