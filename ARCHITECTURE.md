         +-----------------------------+
         |        Client / CLI         |
         +-------------+---------------+
                       |
                       v
         +-------------+---------------+
         |         Coordinator Node     |
         |  - Knows cluster topology    |
         |  - Handles client requests   |
         |  - Routes to correct node    |
         +-------------+---------------+
                       |
       ----------------+------------------
      |                |                  |
+-----+-----+    +-----+-----+     +------+------+
|   Node A  |    |   Node B  |     |   Node C     |
| - KV store|    | - KV store|     | - KV store   |
| - Replica |    | - Replica |     | - Replica    |
+-----------+    +-----------+     +-------------+