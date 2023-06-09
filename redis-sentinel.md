# Redis Sentinel
## docker 실행 
- docker-compose.yml 파일이 있는 폴더에서 다음 명령어를 사용하여 docker를 실행시킨다.
```bash
$ docker-compose up --scale redis-sentinel=3 -d
[+] Running 8/8
 ⠿ Network hee-commerce_redis-network       Created                                                                     0.0s
 ⠿ Container redis-master                   Started                                                                     0.7s
 ⠿ Container redis-slave-2                  Started                                                                     1.1s
 ⠿ Container redis-slave-1                  Started                                                                     1.3s
 ⠿ Container redis-slave-3                  Started                                                                     1.3s
 ⠿ Container hee-commerce-redis-sentinel-3  Started                                                                     2.1s
 ⠿ Container hee-commerce-redis-sentinel-1  Started                                                                     1.8s
 ⠿ Container hee-commerce-redis-sentinel-2  Started                                                                     1.5s

```

## Master
- Docker Desktop을 이용해서 Master를 다운 시키고 난 후 Slave 에서 무슨 일이 일어나는지 log 로 확인하고, sentinel로 승격된 Master를 확인한다.


## Slave
- 다음 명령어를 사용하여 `redis-slave-1` 컨테이너의 로그를 확인한다.
```bash
$ docker logs -f redis-slave-1
```
- 다음 명령어를 사용하여 `redis-slave-2` 컨테이너의 로그를 확인한다.
```bash
$ docker logs -f redis-slave-2
```
- 다음 명령어를 사용하여 `redis-slave-3` 컨테이너의 로그를 확인한다.
```bash
$ docker logs -f redis-slave-3
```


## sentinel
- 다음 명령어를 사용하여 `hee-commerce-redis-sentinel-1` 컨테이너에 접속한다.
```bash
$ docker exec -it hee-commerce-redis-sentinel-1 bash
```
- 다음 명령어를 사용하여 `hee-commerce-redis-sentinel-1` 컨테이너 내의 Redis 서버에 접속한다. 
```bash
I have no name!@dff47e43923b:/$ redis-cli -h hee-commerce-redis-sentinel-1 -p 26379
```
- `info` 등의 명령어를 사용하기 위해 인증을 한다.
```bash
hee-commerce-redis-sentinel-1:26379> AUTH abcdef
OK
```
- 
```bash
hee-commerce-redis-sentinel-1:26379> info
# Server
redis_version:7.0.11
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:e3f993d7cb5c4569
redis_mode:sentinel
os:Linux 5.15.49-linuxkit x86_64
arch_bits:64
monotonic_clock:POSIX clock_gettime
multiplexing_api:epoll
atomicvar_api:c11-builtin
gcc_version:10.2.1
process_id:1
process_supervised:no
run_id:96092aa49defa1c2ce13b675992d498572b2ac86
tcp_port:26379
server_time_usec:1686317674783940
uptime_in_seconds:1098
uptime_in_days:0
hz:17
configured_hz:10
lru_clock:8596074
executable:/redis-sentinel
config_file:/opt/bitnami/redis-sentinel/etc/sentinel.conf
io_threads_active:0

# Clients
connected_clients:3
cluster_connections:0
maxclients:10000
client_recent_max_input_buffer:20480
client_recent_max_output_buffer:0
blocked_clients:0
tracking_clients:0
clients_in_timeout_table:0

# Stats
total_connections_received:6
total_commands_processed:3864
instantaneous_ops_per_sec:4
total_net_input_bytes:251346
total_net_output_bytes:31051
total_net_repl_input_bytes:0
total_net_repl_output_bytes:0
instantaneous_input_kbps:0.29
instantaneous_output_kbps:0.04
instantaneous_input_repl_kbps:0.00
instantaneous_output_repl_kbps:0.00
rejected_connections:0
sync_full:0
sync_partial_ok:0
sync_partial_err:0
expired_keys:0
expired_stale_perc:0.00
expired_time_cap_reached_count:0
expire_cycle_cpu_milliseconds:25
evicted_keys:0
evicted_clients:0
total_eviction_exceeded_time:0
current_eviction_exceeded_time:0
keyspace_hits:0
keyspace_misses:0
pubsub_channels:0
pubsub_patterns:0
pubsubshard_channels:0
latest_fork_usec:0
total_forks:0
migrate_cached_sockets:0
slave_expires_tracked_keys:0
active_defrag_hits:0
active_defrag_misses:0
active_defrag_key_hits:0
active_defrag_key_misses:0
total_active_defrag_time:0
current_active_defrag_time:0
tracking_total_keys:0
tracking_total_items:0
tracking_total_prefixes:0
unexpected_error_replies:0
total_error_replies:5
dump_payload_sanitizations:0
total_reads_processed:3550
total_writes_processed:3546
io_threaded_reads_processed:0
io_threaded_writes_processed:0
reply_buffer_shrinks:6
reply_buffer_expands:0

# CPU
used_cpu_sys:4.458176
used_cpu_user:2.933645
used_cpu_sys_children:0.077663
used_cpu_user_children:0.065735
used_cpu_sys_main_thread:4.448366
used_cpu_user_main_thread:2.929999

# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_tilt_since_seconds:-1
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redis-master-set,status=odown,address=192.168.0.4:6379,slaves=4,sentinels=3
```


```bash
hee-commerce-redis-sentinel-1:26379> sentinel masters
1)  1) "name"
    2) "redis-master-set"
    3) "ip"
    4) "192.168.0.4"
    5) "port"
    6) "6379"
    7) "runid"
    8) "d2f79c43eddec1b1e0cfcb21c8dbf919fd65db7d"
    9) "flags"
   10) "s_down,o_down,master"
   11) "link-pending-commands"
   12) "85"
   13) "link-refcount"
   14) "1"
   15) "last-ping-sent"
   16) "310196"
   17) "last-ok-ping-reply"
   18) "311103"
   19) "last-ping-reply"
   20) "311103"
   21) "s-down-time"
   22) "307096"
   23) "o-down-time"
   24) "307032"
   25) "down-after-milliseconds"
   26) "3000"
   27) "info-refresh"
   28) "315331"
   29) "role-reported"
   30) "master"
   31) "role-reported-time"
   32) "941755"
   33) "config-epoch"
   34) "3"
   35) "num-slaves"
   36) "4"
   37) "num-other-sentinels"
   38) "2"
   39) "quorum"
   40) "2"
   41) "failover-timeout"
   42) "180000"
   43) "parallel-syncs"
   44) "1"
```

```bash
hee-commerce-redis-sentinel-1:26379> sentinel get-master-addr-by-name redis-master-set
1) "192.168.0.4"
2) "6379"
```