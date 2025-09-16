import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'like_model.dart';
import 'post_model.dart';

class PostDetailScreen extends StatefulWidget {
  final List<Post> posts; // 전체 게시글 목록
  final int initialIndex; // 시작 인덱스

  const PostDetailScreen({
    required this.posts,
    required this.initialIndex,
    super.key,
  });

  @override
  State<PostDetailScreen> createState() => _PostDetailScreenState();
}

class _PostDetailScreenState extends State<PostDetailScreen> {
  late PageController _pageController;
  late int _currentIndex;

  @override
  void initState() {
    super.initState();
    _currentIndex = widget.initialIndex;
    _pageController = PageController(initialPage: _currentIndex);
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final likeModel = Provider.of<LikeModel>(context);

    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: PageView.builder(
          scrollDirection: Axis.vertical,
          controller: _pageController,
          itemCount: widget.posts.length,
          onPageChanged: (idx) {
            setState(() {
              _currentIndex = idx;
            });
          },
          itemBuilder: (context, index) {
            final post = widget.posts[index];
            final isLiked = likeModel.isLiked(post);
            return Stack(
              children: [
                Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Expanded(
                      child: Center(
                        child: AspectRatio(
                          aspectRatio: 1,
                          child: Image.file(post.image, fit: BoxFit.contain),
                        ),
                      ),
                    ),
                    SizedBox(height: 24),
                    Text(
                      post.region,
                      style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.bold),
                      textAlign: TextAlign.center,
                    ),
                    SizedBox(height: 8),
                    Text(
                      _formatDate(post.date),
                      style: TextStyle(color: Colors.white60, fontSize: 13),
                      textAlign: TextAlign.center,
                    ),
                    SizedBox(height: 16),
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 20.0),
                      child: Text(
                        post.desc,
                        style: TextStyle(color: Colors.white, fontSize: 16),
                        textAlign: TextAlign.center,
                      ),
                    ),
                    SizedBox(height: 40),
                  ],
                ),
                Positioned(
                  right: 24,
                  bottom: 64,
                  child: IconButton(
                    icon: Icon(
                      isLiked ? Icons.favorite : Icons.favorite_border,
                      color: isLiked ? Colors.red : Colors.white,
                      size: 36,
                    ),
                    onPressed: () => likeModel.toggle(post),
                  ),
                ),
                // ← 여기서 index 안내(예: Positioned(top: ..., right: ..., ...) 부분을 완전히 삭제!
              ],
            );
          },
        ),
      ),
    );
  }

  String _formatDate(DateTime dt) {
    return '${dt.year}.${dt.month.toString().padLeft(2, '0')}.${dt.day.toString().padLeft(2, '0')} '
        '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }
}
