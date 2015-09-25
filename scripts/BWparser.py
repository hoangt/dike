
import csv
import sys


def avgCol(col,RunNumber):
	addr="../Results/mix5/run"+str(RunNumber)+"/"
	ifile  = open(addr+'ProgsBW.csv', "rb")
	reader = csv.reader(ifile)
	next(reader)
	the_numbers = [float(row[i]) for row in reader]
	average = sum(the_numbers) / len(the_numbers)

	ifile.close()
	return average



RunNumber = int(sys.argv[1])
averages=[]
for i in range(0,41):
	avg=avgCol(i,RunNumber)
	if (avg!=0):
		averages.append(avg)

line="run"+str(RunNumber)
for avg in averages:
	line=line+str(avg)+","

print line.strip()[:-1]

