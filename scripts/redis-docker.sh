docker pull redis:7.0.8
docker exec -it myredis bash
cd /usr/local/etc/redis
redis-cli -a redis-pass