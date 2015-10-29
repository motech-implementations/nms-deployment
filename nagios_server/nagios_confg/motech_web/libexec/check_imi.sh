STATE_OK=0              # define the exit code if status is OK
STATE_WARNING=1         # define the exit code if status is Warning (not really used)
STATE_CRITICAL=2        # define the exit code if status is Critical
STATE_UNKNOWN=3         # define the exit code if status is Unknown
export PATH=$PATH:/usr/local/bin:/usr/bin:/bin # Set path

nc ${1} ${2} </dev/null
if [ $? = 0 ]; then
	echo "STATUS OK "
        exit 0;
else
	echo "NOK: $1 is not reachable on port $2"
        exit 2;
fi

