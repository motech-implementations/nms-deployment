#!/usr/bin/php -q
<?php
$Status=0;
$Timeout=10;
$ch = curl_init();
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_HEADER,0);
curl_setopt($ch, CURLOPT_TIMEOUT, $Timeout);
curl_setopt($ch, CURLOPT_URL,"http://192.168.200.1:8080/motech-platform-server/module/alerts/newAlertCount");

if( ! $Buff=curl_exec($ch))
{
	echo "Error in Execution of Command";
	$Status=2;
}

$reqInfo = curl_getinfo($ch);
$errnum=curl_errno($ch);
if ($errnum){
	if ($errnum==28){
        	echo 'Timeout '.$Timeout.'sec exceeded.';
                exit(2);
    	}else{
               echo "ERROR in opening page! Err:".curl_error($ch);
      	       exit(2);
    }
  }
if ( $Buff > 0)
{
        echo "There are $Buff New Alerts";
        $Status=2;
}


@curl_close($ch);
exit($Status)
?>
