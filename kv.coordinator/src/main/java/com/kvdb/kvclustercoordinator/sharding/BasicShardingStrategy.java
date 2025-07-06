package com.kvdb.kvclustercoordinator.sharding;

import com.kvdb.kvclustercoordinator.cluster.ClusterNode;

import java.util.List;

public class BasicShardingStrategy implements ShardingStrategy {

    int shardIdx = 0;

    @Override
    public ClusterNode getShardWithKey(String key, List<ClusterNode> nodes) {
        int shardIndex = Math.abs(key.hashCode()) % nodes.size();
        return nodes.get(shardIndex);
    }

    @Override
    public ClusterNode getShard(List<ClusterNode> nodes) {
        shardIdx = (shardIdx + 1) % nodes.size();
        return nodes.get(shardIdx);
    }
}
