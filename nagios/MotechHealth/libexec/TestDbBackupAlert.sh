#!/usr/bin/env bash

set -x

#declare dir=$1
declare size=$2
declare days=$3

size='+10M'
days='0'
typeset -A Path_Incremental=([motech2]=/mnt/nasbkp/motech_db2/Incrementaldbbackup  [report2]=/mnt/nasbkp/report_db2/Incrementaldbbackup) ;
typeset -A Path_FullBackup=([motech2]=/mnt/nasbkp/motech_db2/Fulldbbackup [report2]=/mnt/nasbkp/report_db2/Fulldbbackup) ;
declare path="";

#echo "${!Path_Incremental[@]}"


if [ $1 = 1 ]
then
	for key in "${!Path_Incremental[@]}"; 
	do 
		echo " ${Path_Incremental[$key]}"; 
		no_of_files=$(find "${Path_Incremental[$key]}" -type f -size "$size"  -mtime $days -print  | rev |awk -F '/' ' {print $1}' | rev | wc -l)
	    
		if [ $no_of_files -lt 1 ]
	 	then
        		path=$(echo ${Path_Incremental[$key]} )
			break;
	       	fi
	done
else
	for key in "${!Path_FullBackup[@]}";
	do
        	echo " ${Path_FullBackup[$key]}";
	        no_of_files=$(find "${Path_FullBackup[$key]}" -type f -size "$size"  -mtime $days -print  | rev |awk -F '/' ' {print $1}' | rev | wc -l)

        if [ $no_of_files -lt 1 ]
        then
                path=$(echo ${Path_FullBackup[$key]} )
                break;
        fi
	done
fi

#no_of_files=$(find "$1" -type f -size "$2"  -mtime $3 -print  | rev |awk -F '/' ' {print $1}' | rev | wc -l)
if [ $no_of_files -gt 0 ]
  then
	echo "DB Backup Exists in All Servers"
        exit 0
  else
	echo "DB Backup IN $path does not Exists"
        exit 2
fi

