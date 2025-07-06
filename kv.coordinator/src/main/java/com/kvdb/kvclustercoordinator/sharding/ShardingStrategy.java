package com.kvdb.kvclustercoordinator.sharding;

import com.kvdb.kvclustercoordinator.cluster.ClusterNode;

import java.util.List;

public interface ShardingStrategy {
    /**
     * Determines the shard for a given key.
     *
     * @param key the key to be sharded
     * @return the shard
     */
    ClusterNode getShardWithKey(String key, List<ClusterNode> nodes);
    ClusterNode getShard(List<ClusterNode> nodes);

}
