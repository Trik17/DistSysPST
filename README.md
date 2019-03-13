# DistSysPST - Distributed Systems
# Replicated data storage
Implement a replicated data storage.\
For simplicity assume that the data to be replicated are integer numbers, each identified by a unique id. \
N servers keep a copy of the data shared by clients, offering two primitives:\
int read(dataId); \
void write(dataId, newValue); \
The client may connect to any of these servers (a single one for the entire session). Servers cooperate to keep a consistent, replicated copy of the shared data. In particular, the system must provide a causal consistency model. \
The following assumptions hold: \
Servers are reliable and known to each other. \
Severs run on the same subnet (IP multicast is available among them). \
Channels are unreliable. 

Suggestion: may use a totally ordered multicast primitive (possibly built on top of IP multicast) to guarantee the consistency requirements.\
Implement the project in Java (or simulate it in OmNet++).\
Optional: Relax the assumptions about servers being known and reliable.
