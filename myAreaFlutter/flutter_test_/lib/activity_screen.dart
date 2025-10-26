import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';

import 'post_model.dart';
import 'post_detail_screen.dart';

class ActivityScreen extends StatelessWidget {
  final List<Post> posts;

  const ActivityScreen({super.key, required this.posts});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('지도에서 보기')),
      body: FlutterMap(
        options: MapOptions(
          initialCenter: posts.isNotEmpty
              ? LatLng(posts.last.latitude, posts.last.longitude)
              : const LatLng(37.5665, 126.9780), // 기본: 서울 시청
          initialZoom: 13,
          interactionOptions: const InteractionOptions(
            flags: InteractiveFlag.pinchZoom | InteractiveFlag.drag,
          ),
        ),
        children: [
          TileLayer(
            urlTemplate: 'https://a.tile.openstreetmap.de/{z}/{x}/{y}.png',
            userAgentPackageName: 'com.example.app',
          ),
          MarkerLayer(
            markers: posts.asMap().entries.map((entry) {
              final index = entry.key;
              final post = entry.value;

              return Marker(
                point: LatLng(post.latitude, post.longitude),
                width: 60,
                height: 60,
                child: GestureDetector(
                onTap: () {
                  final sameRegionPosts = posts
                      .where((p) => p.region == post.region)
                      .toList()
                    ..sort((a, b) => b.date.compareTo(a.date)); // 최신순 정렬

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
