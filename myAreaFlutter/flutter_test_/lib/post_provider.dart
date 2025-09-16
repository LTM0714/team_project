import 'dart:io';
import 'package:flutter/foundation.dart';
import 'post_model.dart';

class PostProvider extends ChangeNotifier {
  List<Post> _myPosts = [];
  List<Post> _likedPosts = [];
  File? _profileImage;
  String _intro = '자기소개를 입력하세요.';
  List<String> _interestRegions = [];
  final List<String> _allRegions = ['서울', '부산', '대구', '대전', '광주', '인천'];

  List<Post> get myPosts => _myPosts;
  List<Post> get likedPosts => _likedPosts;
  File? get profileImage => _profileImage;
  String get intro => _intro;
  List<String> get interestRegions => _interestRegions;
  List<String> get allRegions => _allRegions;

  // 내 게시글 추가
  void addPost(Post post) {
    _myPosts.add(post);
    notifyListeners();
  }

  // 좋아요 추가/삭제
  void toggleLike(Post post) {
    if (_likedPosts.contains(post)) {
      _likedPosts.remove(post);
    } else {
      _likedPosts.add(post);
    }
    notifyListeners();
  }

  // 프로필 사진 수정
  void updateProfileImage(File? newImage) {
    _profileImage = newImage;
    notifyListeners();
  }

  // 소개글 수정
  void updateIntro(String newIntro) {
    _intro = newIntro;
    notifyListeners();
  }

  // 관심지역 수정
  void updateInterestRegions(List<String> newRegions) {
    _interestRegions = newRegions;
    notifyListeners();
  }
}
