#!/usr/bin/python

import requests
import sys
import os.path
import time

status=0
url='http://192.168.200.3:8080/motech-platform-server/module/mds/rest/alertsmodule/alert'
Critical_Count = 0
High_Count = 0
Medium_Count = 0
Low_Count = 0
File='/tmp/AllmotechAlert.csv'
Header="DATE TIME"+","+"id"+","+"priority"+","+"alertType"+","+"name"+","+"description"
#Procedure to request motech URL
def request_alerts(page=None):
  params = None
  if page:
    params = {'page': page}
  r = requests.get(url, params=params, auth=('motech', 'motech@123'))
  if r.status_code != 200:
    status=2
    if page:
      print "Error while accesing MOTECH ALERTS at page %d with error %d" % (page, p.status_code) 
    else:
      print "Error while accessing MOTECH ALERTS %d" % r.status_code
    sys.exit(status)
  else:
    return r
#remove motechalert.csv from /tmp if exists
 
if ( os.path.exists(File) ):
	os.remove(File)

r = request_alerts()
#Fetch the Total pages
#Check Response code
data = r.json()
  #Calculate the Total Pages which needs to be analayzed
if (data['metadata']['totalCount'] > 0):
  status=2
  f = open(File,'a+')
  f.write(Header)
  f.write('\n')
  pageCount=data['metadata']['totalCount']/data['metadata']['pageSize']  
  reminder=data['metadata']['totalCount']%data['metadata']['pageSize']
  #Add one more page if TotalAlerts is not in multiple of pagesize
  if reminder > 0:
    pageCount = pageCount + 1
    for index in range(1,pageCount+1):
      #Fetch Records page wise starting page 1
      p = request_alerts(index)
      pr = p.json()
      #Update the loopcount as per the number of alerts in the page
      if (index == pageCount and reminder > 0):
        loopCount = reminder
      else:
        loopCount= pr['metadata']['pageSize']
      for count in range(0,loopCount):
        if ((pr['data'][count]['status']) =='NEW'):
          if ((pr['data'][count]['alertType']) =='CRITICAL'):
            Critical_Count = Critical_Count + 1
          if ((pr['data'][count]['alertType']) =='HIGH'):
            High_Count = High_Count + 1
          if ((pr['data'][count]['alertType']) =='MEDIUM'):
        	  Medium_Count = Medium_Count + 1
          if ((pr['data'][count]['alertType']) =='LOW'):
        	  Low_Count = Low_Count + 1
          datetime=time.strftime('%d-%b-%y %H:%M:%S', time.localtime((pr['data'][count]['creationDate'])/1000))
          alert=datetime+","+str((pr['data'][count]['id']))+","+str((pr['data'][count]['priority']))+","+(pr['data'][count]['alertType'])+","+(pr['data'][count]['name'])+","+(pr['data'][count]['description'])
          f.write(alert)
          f.write('\n')

    f.close()
    print "There are total %d New Alerts out of which ::CRITICAL:%d, HIGH:%d, MEDIUM:%d and LOW:%d" %(data['metadata']['totalCount'],Critical_Count,High_Count,Medium_Count,Low_Count)

  else:
    status=0;
    print"No Alerts in system"
sys.exit(status)
