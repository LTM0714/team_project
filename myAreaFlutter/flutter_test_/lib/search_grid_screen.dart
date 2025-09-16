import 'package:flutter/material.dart';
import 'post_model.dart';
import 'post_detail_screen.dart';

class SearchGridScreen extends StatelessWidget {
  final List<Post> posts;
  final List<String> interestRegions;

  const SearchGridScreen({
    required this.posts,
    required this.interestRegions,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final feed = [...posts]..retainWhere((post) => interestRegions.contains(post.region));
    feed.sort((a, b) => b.date.compareTo(a.date));

    return Scaffold(
      appBar: AppBar(title: Text('사진 피드')),
      body: feed.isEmpty
          ? Center(child: Text('관심지역에 등록된 사진이 없습니다.'))
          : GridView.builder(
              padding: EdgeInsets.all(2),
              gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3,
                crossAxisSpacing: 2,
                mainAxisSpacing: 2,
                childAspectRatio: 1,
              ),
              itemCount: feed.length,
              itemBuilder: (context, index) {
                final post = feed[index];
                return GestureDetector(
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (_) => PostDetailScreen(
                          posts: feed,      // 현재 피드 전체를 전달
                          initialIndex: index, // 현재 인덱스 전달
                        ),
                      ),
                    );
                  },
                  child: Stack(
                    children: [
                      Positioned.fill(
                        child: Image.file(post.image, fit: BoxFit.cover),
                      ),
                      Positioned(
                        left: 5,
                        bottom: 5,
                        child: Container(
                          padding: EdgeInsets.symmetric(
                              vertical: 2, horizontal: 7),
                          decoration: BoxDecoration(
                            color: Colors.black.withOpacity(0.6),
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: Text(
                            post.region,
                            style: TextStyle(
                                color: Colors.white, fontSize: 12),
                          ),
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
    );
  }
}
