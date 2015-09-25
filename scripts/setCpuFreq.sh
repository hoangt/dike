

for i in `seq 0 9`; do  
	sudo cpufreq-set -c $i -g userspace ;
	echo $i;
	sudo cpufreq-set -c $i -f 1200000 ;
	echo 1200000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_min_freq ; 
	echo 1200000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_max_freq ;
done


for i in `seq 10 19`; do  
	sudo cpufreq-set -c $i -g userspace ;
	echo $i;
	sudo cpufreq-set -c $i -f 3000000 ;
	echo 3000000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_min_freq ; 
	echo 3000000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_max_freq ;
done

for i in `seq 20 29`; do  
	sudo cpufreq-set -c $i -g userspace ;
	echo $i;
	sudo cpufreq-set -c $i -f 1200000 ;
	echo 1200000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_min_freq ; 
	echo 1200000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_max_freq ;
done

for i in `seq 30 39`; do  
	sudo cpufreq-set -c $i -g userspace ;
	echo $i;
	sudo cpufreq-set -c $i -f 3000000 ;
	echo 3000000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_min_freq ; 
	echo 3000000 | sudo tee /sys/devices/system/cpu/cpu$i/cpufreq/scaling_max_freq ;
done

sleep 2
printf "  --------------------------------------------------  " 
for i in `seq 0 39`; do  
	printf "$i : " 
	sudo cat /sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_cur_freq

done
