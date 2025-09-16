import 'package:flutter/material.dart';
import 'post_model.dart';

class LikeModel extends ChangeNotifier {
  final Set<DateTime> _likedPostDates = {};

  // ✅ 추가: 실제 Post를 보관해 두기 위한 맵 (date를 키로 사용)
  final Map<DateTime, Post> _likedPostsByDate = {};

  bool isLiked(Post post) => _likedPostDates.contains(post.date);

  void toggle(Post post) {
    if (_likedPostDates.contains(post.date)) {
      _likedPostDates.remove(post.date);
      _likedPostsByDate.remove(post.date); // ✅ 추가: 맵에서도 제거
    } else {
      _likedPostDates.add(post.date);
      _likedPostsByDate[post.date] = post; // ✅ 추가: 맵에 보관
    }
    notifyListeners();
  }

  List<DateTime> get likedPostDates => _likedPostDates.toList();

  // ✅ 추가: main.dart에서 사용하는 likedPosts 게터
  List<Post> get likedPosts => _likedPostsByDate.values.toList();
}
