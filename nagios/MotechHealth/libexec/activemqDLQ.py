#!/usr/bin/python

import requests
import xml.etree.ElementTree as ET
import sys
status=0
broker1url='http://192.168.200.6:8161/admin/xml/queues.jsp'
broker2url='http://192.168.200.5:8161/admin/xml/queues.jsp'
Critical_Count = 0
High_Count = 0
Medium_Count = 0
Low_Count = 0
Fresh_Alert=0
STATUS_OK=0
STATUS_WARNING=1
STATUS_CRITICAL=2

#This Procedure Checks if passed URL is working or not, If URL is working than
#it finds if there is any message in ActtiveMQ Dead letter Queue
# RETURN CODE
#       SUCCESS                              ---> 0
#       URL NOT WORKING                      ---> 2
#       URL WORKING BUT SOME MESSAGES in DLQ ---> 1

def request_url(url):
  return_status=0
  params = None
  try:
    req = requests.get(url, params=params, auth=('admin','admin'))
    if (req.status_code == 200):
      root = ET.fromstring(req.text)
      for child in root:
        if child.tag == 'queue' and child.attrib['name']=='ActiveMQ.DLQ':
          for c in child:
            if (c.tag == 'stats' and c.attrib['size'] != '0'):
              print "ActiveMQ Dead Letter Queue has %s messages" % (c.attrib['size'])
              return_status=1
              break
            else:
              return_status=0
    else:
        print "Request failed with response code %s" % (req.status_code)
        return_status=2
  except requests.exceptions.ConnectionError:
    return_status=2
  return return_status

def request_alerts():
  return_value=request_url(broker2url)
  if (return_value == 2):
    return_value=request_url(broker1url)
    if (return_value == 2):
      print "Both Brokers are down"
      status=STATUS_CRITICAL
    else:
      if (return_value == 1):
        status=STATUS_CRITICAL
      else:
        status=STATUS_OK
        print "No Messages in ActiveMQ DLQ"
  else:
    if (return_value == 1):
      status=STATUS_CRITICAL
    else:
      status=STATUS_OK
      print "No Messages in ActiveMQ DLQ"
  sys.exit(status)

if __name__ == "__main__":
	request_alerts()
	
	
