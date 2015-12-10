#!/bin/bash
#
# Facette
#
# chkconfig: 3 50 50
# description: Facette init.d
DESC="facette server"
NAME=facette
DAEMON=/usr/local/bin/facette
PIDFILE=/var/run/$NAME/$NAME.pid
SCRIPTNAME=/etc/init.d/$NAME
LOG=/var/log/facette.log
RETVAL=0

start() {
    echo -n $"Starting ${NAME}: "

    # See if it's already running. Look *only* at the pid file.
    if [ -f ${PIDFILE} ] && kill -SIGCONT $(cat ${PIDFILE}) 2> /dev/null; then
        echo "PID file exists and process is running for ${NAME}"
        RETVAL=1
    else
        # Run as process
        su - facette -c ${DAEMON} &> ${LOG} &
        RETVAL=$?

        # Success
        [ $RETVAL = 0 ] && echo "${NAME} started"
    fi

    echo
    return ${RETVAL}
}

stop() {
    if [ ! -f ${PIDFILE} ] || ! kill -SIGCONT $(cat ${PIDFILE}) 2> /dev/null; then
        echo "${NAME} is not running."
        return 1
    fi

    echo -n $"Stopping ${NAME}: "

    if
        kill $(cat ${PIDFILE})
    then
        echo "Done"
        return 0
    fi

    echo "Failed!"
    return 1
}

# See how we were called.
case "${1}" in
  start)
    start
    ;;
  stop)
    stop
    ;;
  status)
    echo -n "Status: "

    if
        [ "$(cat ${PIDFILE})" == "" ]
    then
        echo "Stopped"
        RETVAL=1
    elif
        kill -0 $(cat ${PIDFILE}) &> /dev/null
    then
        echo "Running (pid: $(cat ${PIDFILE}))"
        RETVAL=0
    else
        echo "Dead (pid was: $(cat ${PIDFILE}))"
        RETVAL=2
    fi
    ;;
  restart)
    stop
    start
    ;;
  condrestart)
    if [ -f ${PIDFILE} ] ; then
        stop
        start
    fi
    ;;
  *)
    echo $"Usage: ${NAME} {start|stop|restart|condrestart|status}"
    exit 1
esac

exit ${RETVAL}
