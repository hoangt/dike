
Dike - Adaptive Fair Scheduler

Project Description:
This a adaptive contention-aware scheduler that runs in software level, maximize fairness and performance of systems. (Especially heterogeneous structure)


Running Example:
First make sure that these packages are installed on your machine:
0) java 

1) perf : Linux profiling with performance counters (https://perf.wiki.kernel.org/index.php/Main_Page)

2) (optional) cpufrequtils: Dynamic CPU frequency scaling (http://www.thinkwiki.org/wiki/How_to_use_cpufrequtils)

[You need cpufrequtils if you want to change your homogeneous system into heterogeneous structure by scaling up/down CPU frequencies ]

3) (optional) numactl : Control NUMA policy for processes or shared memory (http://linux.die.net/man/8/numactl) 

How to run:

$ make

$ make run


Developing Source Code:
All files are in "src/" directory. 

Just testing again



