import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'like_model.dart';
import 'post_model.dart';
import 'post_detail_screen.dart';

class ActivityScreen extends StatelessWidget {
  final List<Post> allPosts;
  const ActivityScreen({required this.allPosts, super.key});

  @override
  Widget build(BuildContext context) {
    final likeModel = Provider.of<LikeModel>(context);
    final likedPosts = allPosts.where((post) => likeModel.isLiked(post)).toList()
      ..sort((a, b) => b.date.compareTo(a.date));
    return Scaffold(
      appBar: AppBar(title: Text('좋아요한 사진')),
      body: likedPosts.isEmpty
          ? Center(child: Text('좋아요한 사진이 없습니다.'))
          : ListView.builder(
              itemCount: likedPosts.length,
              itemBuilder: (context, index) {
                final post = likedPosts[index];
                return GestureDetector(
                  onTap: () {
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (_) => PostDetailScreen(
                          posts: likedPosts,
                          initialIndex: index,
                        ),
                      ),
                    );
                  },
                  child: Card(
                    margin: EdgeInsets.all(8),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.stretch,
                      children: [
                        AspectRatio(
                          aspectRatio: 1,
                          child: Image.file(post.image, fit: BoxFit.cover),
                        ),
                        Padding(
                          padding: EdgeInsets.all(10),
                          child: Text(post.desc, style: TextStyle(fontSize: 15)),
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
    );
  }
}
