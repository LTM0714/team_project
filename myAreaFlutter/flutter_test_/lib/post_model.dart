import 'dart:io';

class Post {
  final File image;
  final String region;
  final String desc;
  final DateTime date;

  Post({
    required this.image,
    required this.region,
    required this.desc,
    required this.date,
  });
}
