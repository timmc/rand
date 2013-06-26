# Generate historical hash data

```
$ lein run -m rand.geohash.slurp-djia cached/djia.sclj 
$ lein run -m rand.geohash.points cached/djia.sclj east cached/points-east.sclj
$ lein run -m rand.geohash.points cached/djia.sclj west cached/points-west.sclj
$ lein run -m rand.geohash.to-csv cached/points-east.sclj cached/points-east.csv
$ lein run -m rand.geohash.to-csv cached/points-west.sclj cached/points-west.csv
```
