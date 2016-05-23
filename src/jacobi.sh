###############################################################################
#You need to change these params before running the script
benchmarkAddr="/local/inspector/benchmark"
resultsAddr="/local/Results"

#How to run:
#./jacobi.sh 8 500 0 1 jacobi
# where swapSize is 8, quantaLength is 500 ms. Dike Scheduler is not activated. 
#RunIndex (in case you want to run 10 runs consecutively) is 1, and initial layout of running apps are "jacobi" in the cores.layout file.

#Inputs from command line
swapSize=$1
migQuanta=$2
schedActivate=$3
runindex=$4
layout=$5


###############################################################################
###############################################################################
###############################################################################

function storeLogs (){
	mkdir -p ../Results/run$runindex
	for (( i = 0; i <= $((SampleSize-1)); i++ ))
	do
    	mv ${benchmark[i]}.log $resultsAddr/run$runindex
	done 
	mv CoresSpeed.csv $resultsAddr/run$runindex
	mv ProgsBW.csv $resultsAddr/run$runindex
	python ../scripts/jacobiOutputParser.py $SampleSize $runindex 

	me=`basename "$0"`
	if [ $schedActivate -eq 0 ]
	then
   		sched="NoSched"
   	else
		sched="WithSched"
	fi

	echo "run$runindex -> $me : swapSize=$swapSize : migFrequ=$migQuanta : $sched" >> $resultsAddr/RunSummary.txt

}

###############################################################################

benchmark[0]="jacobi0"
benchmark[1]="jacobi1"
benchmark[2]="jacobi2"
benchmark[3]="jacobi3"

cmd[0]="numactl --interleave=0 --physcpubind=0 $benchmarkAddr/jacobi0 >${benchmark[0]}.log &"
cmd[1]="numactl --interleave=0 --physcpubind=1 $benchmarkAddr/jacobi1 >${benchmark[1]}.log &"
cmd[2]="numactl --interleave=0 --physcpubind=2 $benchmarkAddr/jacobi2 >${benchmark[2]}.log &"
cmd[3]="numactl --interleave=0 --physcpubind=3 $benchmarkAddr/jacobi3 >${benchmark[3]}.log &"

benchmark[4]="jacobi4"
benchmark[5]="jacobi5"
benchmark[6]="jacobi6"
benchmark[7]="jacobi7"

cmd[4]="numactl --interleave=0 --physcpubind=10 $benchmarkAddr/jacobi4 >${benchmark[4]}.log &"
cmd[5]="numactl --interleave=0 --physcpubind=11 $benchmarkAddr/jacobi5 >${benchmark[5]}.log &"
cmd[6]="numactl --interleave=0 --physcpubind=12 $benchmarkAddr/jacobi6 >${benchmark[6]}.log &"
cmd[7]="numactl --interleave=0 --physcpubind=13 $benchmarkAddr/jacobi7 >${benchmark[7]}.log &"

###############################################################################
###############################################################################
###############################################################################
###############################################################################
###############################################################################

swapSize=$1
migQuanta=$2
schedActivate=$3
runindex=$4
layout=$5

SampleSize=8

export OMP_NUM_THREADS=1;

for (( i = 0; i <= $((SampleSize-1)); i++ ))
do
    eval ${cmd[$i]}
done

sleep 1

for (( i = 0; i <= $((SampleSize-1)); i++ ))
do
    pid[$i]=$(pgrep ${benchmark[$i]})
done


for (( i = 0; i <= $((SampleSize-1)); i++ ))
do
	printf "${pid[$i]} | "
done
echo


rm -f inspector.class
javac inspector.java


echo "*************************************"

#Main Run
java inspector $swapSize $migQuanta $schedActivate $layout 8 ${pid[0]} ${pid[1]} ${pid[2]} ${pid[3]} ${pid[4]} ${pid[5]} ${pid[6]} ${pid[7]} 



#Clean UP
sudo pkill needle  
sudo pkill kmeans  
sudo pkill gups  
sudo pkill jacobi
sleep 1
ps aux | grep needle
ps aux | grep kmeans
ps aux | grep gups
ps aux | grep jacobi

storeLogs
#sudo pkill needle; sudo pkill kmeans  ;sudo pkill gups  ;  sudo pkill jacobi


