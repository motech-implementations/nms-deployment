#! /bin/sh

# Get OBD file information from Web 1

/usr/local/nagios/libexec/check_nrpe -H 192.168.200.1 -c check_target_file > /tmp/target_output.txt


# Convert Text file to HTML format

echo "<html>" > /tmp/target_output.html
echo "<head></head>" >> /tmp/target_output.html
echo "<body>" >> /tmp/target_output.html

old_IFS=$IFS
IFS=$'\n'

array=($(cat /tmp/target_output.txt))
IFS=$old_IFS

echo "<p align="left">Hi All, </p><p align="justify">${array[$i]}</p>" >> /tmp/target_output.html

echo "<table align="LEFT" border="1" cellpadding=5 cellspacing=5 frame="border" width="50%">" >> /tmp/target_output.html

for (( i = 0; i < ${#array[@]}; i++ ))
do

	if [ $i -le 0 ]
   	then
   		echo ""
	else
    		echo "<tr>" >> /tmp/target_output.html
    		
		if [ $i -le 1 ]
    		then
     			echo "<td nowrap="nowrap" bgcolor="lavender" align="center" colspan=5>${array[$i]}</td>" >> /tmp/target_output.html
    		else

    			if [ $i -le 2 ]
    			then
     				IFS=' ' read -r -a word <<< "${array[i]}"
				echo "<th bgcolor="orange">"${word[1]}"</th>" >> /tmp/target_output.html
     				echo "<th bgcolor="orange">"STATE"</th>" >> /tmp/target_output.html
     				echo "<th bgcolor="orange">"${word[0]}"</th>" >> /tmp/target_output.html
				echo "<th bgcolor="orange">"${word[2]}"</th>" >> /tmp/target_output.html
				echo "<th bgcolor="orange">"${word[3]}"</th>" >> /tmp/target_output.html
    			else
    				IFS=' ' read -r -a word <<< "${array[i]}"


				#Query to get state name against a circle
                                state_name=$(ssh -p 1027 grameen@192.168.200.5 'mysql -u etluser -petl@2015! -N -e "use motech_data_services;select name from nms_states where id IN (select distinct(state_id_OID) from nms_districts where circle_id_OID = (select id from nms_circles where name='"'${word[1]}'"'));"') 
				echo "<td width="30%">${word[1]}</td>" >> /tmp/target_output.html
				echo "<td width="70%">"$state_name"</td>" >> /tmp/target_output.html
     				echo "<td width="30%">"${word[0]}"</td>" >> /tmp/target_output.html
				echo "<td width="30%">"${word[2]}"</td>" >> /tmp/target_output.html
			
				if [ "${word[3]}" != "" ]
				then
					echo "<td width="30%">"${word[3]}"</td>" >> /tmp/target_output.html
				else
					echo "<td width="30%">"0"</td>" >> /tmp/target_output.html
				fi
    			fi
	    	fi
    	
		echo "</tr>" >> /tmp/target_output.html
   	fi
 done

#Disclaimer
echo "<tr>" >> /tmp/target_output.html
echo "<td width="100%" colspan=5>"Note : For any circle having multiple states, all its States are mentioned here as finding out exact state will consume a lot of unnecessary time '&' load for DB."</td>" >> /tmp/target_output.html

echo "</tr>" >> /tmp/target_output.html

echo "</table>" >> /tmp/target_output.html

echo "</body>" >> /tmp/target_output.html

echo "</html>" >> /tmp/target_output.html

# Send Email with above created html file

/opt/sendEmail/sendEmail -f motechnagios@ggn.rcil.gov.in -s email.ggn.rcil.gov.in -t anoop.verma@in.bbcmediaaction.org,saurabh.tamrakar@in.bbcmediaaction.org,pnagpal@grameenfoundation.in,Gur_PSS_Grameen_L1@aricent.com,Gur_PSS_Grameen_L2@aricent.com,manjot.singh@aricent.com,fhuster@grameenfoundation.org,bbcsupport@imimobile.com,rahul.gaur@aricent.com,ankit2.shukla@aricent.com,mukesh.roy@aricent.com,sara.chamberlain@in.bbcmediaaction.org -o message-file=/tmp/target_output.html -u "MOTECH TargetFile Notification">/dev/null

#/opt/sendEmail/sendEmail -f motechnagios@ggn.rcil.gov.in -s email.ggn.rcil.gov.in -t rahul.gaur@aricent.com,manjot.singh@aricent.com -o message-file=/tmp/target_output.html -u "MOTECH TargetFile Notification">/dev/null


# Remove created files for sending Email

rm /tmp/target_output.txt
rm /tmp/target_output.html
