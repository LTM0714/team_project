import 'dart:io';

class Post {
  final File image;
  final String region;
  final String desc;
  final DateTime date;
  final double latitude;
  final double longitude;

  Post({
    required this.image,
    required this.region,
    required this.desc,
    required this.date,
    required this.latitude,
    required this.longitude,
  });
}
