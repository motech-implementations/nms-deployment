======================================================================
JvmInspector & check_jvm 
======================================================================

Author: Dimitar Fidanov <dimitar@fidanov.net>
Version: 0.5 (2014-10-14)

======================================================================
Overview
======================================================================

JvmInspector is standalone tool + Nagios wrapper plugin (check_jvm)
that dumps various properties from locally running JVMs.
This information includes:
  * Heap & non-heap memory
  * Running threads
  * Loaded classes
  * Running java version, paths & arguments
  * Container server name (App servers only)
  * Total active sessions (App servers only)
    (tested & supported app servers are tomcat5+ and jboss4+) 

See the examples at the end of this file.

JvmInspector doesn't need local or remote JMX network socket.
It directly attaches to JVM's PerfData, so it must be started with
the same USERid as the target JVM.

The latest version can be found at:
https://fidanov.net/c0d3/nagios-plugins/jvminspector/

Requirements:
  * JDK 1.6+ (for JvmInspector)
  * Bash 3+ (for check_jvm)

Includes:
  * JvmInspector.jar - executable jar package
  * check_jvm - Nagios plugin wrapper script

======================================================================
Installation
======================================================================

JvmInspector is standalone tool and can be placed anywhere, but it's 
required by check_jvm plugin.

Usually the plugin is locally installed on monitored system and executed
through NRPE. The Steps:

* download https://fidanov.net/c0d3/nagios-plugins/jvminspector/JvmInspector.jar
* copy JvmInspector.jar to /usr/local/bin/ on target system.

* copy check_jvm to to Nagios(NRPE) plugins directory on the target 
  system and grant it execute permissions.
  If you have placed JvmInspector.jar in different location then
  "/usr/local/bin/", modify check_jvm script and replace "JVMINSPECTOR"
  with the full path to the jar file.

* edit sudoers file to match the user running NRPE service.
  For example:
  ---
  nagios    ALL=(ALL) NOPASSWD: /usr/lib/nagios/plugins/check_jvm
  ---

* You may also need to do some additional configuration on application
  servers if you want to get container & sessions information.
  For example:
    - tomcat: you don't have to touch anything
    - jboss4: add to run conf: 
        ---
        JAVA_OPTS="$JAVA_OPTS -Djboss.platform.mbeanserver"
        ---
    - jboss7: edit standalone/configuration/standalone.xml
        add after </extensions>:
        ---
        <system-properties>
           <property name="org.apache.tomcat.util.ENABLE_MODELER" value="true"/>
        </system-properties>
        ---

======================================================================
Usage
======================================================================

* JvmInspector - get info from all JVM's (running under same userid)
  $ java -jar /usr/local/bin/JvmInspector.jar all

* check_jvm:
  $ ./check_jvm -n|--name <java_name> -p|--property <property> -w|--warning <warn> -c|--critical <crit>

Where: <propery> is one of: "heap|non-heap|threads|classes|sessions"

You can use "jps -l" or "java -jar JvmInspector.jar all" to get the java_name

Examples:

* test JvmInspector (example with JVM running under user tomcat)
  su - tomcat -c "java -jar /usr/local/bin/JvmInspector.jar all"

* test check_jvm (example with tomcat service running under user tomcat)
  sudo -u tomcat /usr/lib/nagios/plugins/check_jvm.sh -c threads -n org.apache.catalina.startup.Bootstrap -w 100 -c 200

* NRPE config example:

---
command[tomcat_heap]=/usr/bin/sudo -u openbravo /usr/lib/nagios/plugins/check_jvm -n org.apache.catalina.startup.Bootstrap -p heap -w 3758096384 -c 4080218931
command[tomcat_nonheap]=/usr/bin/sudo -u openbravo /usr/lib/nagios/plugins/check_jvm -n org.apache.catalina.startup.Bootstrap -p non-heap -w 268435456 -c 536870912
command[tomcat_classes]=/usr/bin/sudo -u openbravo /usr/lib/nagios/plugins/check_jvm -n org.apache.catalina.startup.Bootstrap -p classes -w 25000 -c 30000
command[tomcat_threads]=/usr/bin/sudo -u openbravo /usr/lib/nagios/plugins/check_jvm -n org.apache.catalina.startup.Bootstrap -p threads -w 200 -c 500
command[tomcat_sessions]=/usr/bin/sudo -u openbravo /usr/lib/nagios/plugins/check_jvm -n org.apache.catalina.startup.Bootstrap -p sessions -w 100 -c 200
---

* Nagios config example:

---
 define service {
   use                 'generic-service'
   service_description 'Tomcat threads count'
   check_command       'check_nrpe!tomcat_threads'
 }
 define service {
   use                 'generic-service'
   service_description 'Tomcat loaded classes count'
   check_command       'check_nrpe!tomcat_classes'
 }
 define service {
   use                 'generic-service'
   service_description 'Tomcat heap memory used'
   check_command       'check_nrpe!tomcat_heap'
 }
 define service {
   use                 'generic-service'
   service_description 'Tomcat non-heap memory used'
   check_command       'check_nrpe!tomcat_nonheap'
 }
 define service {
   use                 'generic-service'
   service_description 'Tomcat total active sessions'
   check_command       'check_nrpe!tomcat_sessions'
 }
---

* Example outputs

$ ./check_jvm -n org.apache.catalina.startup.Bootstrap -p heap -w 1073741824 -c 2147483648

OK 1031655408 |max=1908932608;;; commited=1810366464;;; used=1031655408;;;

$ java -jar JvmInspector.jar all

JVM pid: 7989
  name: org.apache.catalina.startup.Bootstrap start
  thread count: 22 (peak: 22)
  class count: 2533
  heap memory: max=954466304|commited=81788928|used=24899400
  non-heap memory: max=117440512|commited=19136512|used=11680584
  java version: 1.7.0_45 (Oracle Corporation)
  java home: /usr/local/jdk1.7.0_45/jre
  jvm arguments: [-Djava.util.logging.config.file=/usr/local/apache-tomcat-8.0.14/conf/logging.properties, -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager, -Dcom.sun.management.jmxremote=true, -Dcom.sun.management.jmxremote.port=9090, -Dcom.sun.management.jmxremote.ssl=false, -Dcom.sun.management.jmxremote.authenticate=false, -Djava.rmi.server.hostname=192.168.11.1, -Djava.endorsed.dirs=/usr/local/apache-tomcat-8.0.14/endorsed, -Dcatalina.base=/usr/local/apache-tomcat-8.0.14, -Dcatalina.home=/usr/local/apache-tomcat-8.0.14, -Djava.io.tmpdir=/usr/local/apache-tomcat-8.0.14/temp]
  class path: /usr/local/apache-tomcat-8.0.14/bin/bootstrap.jar:/usr/local/apache-tomcat-8.0.14/bin/tomcat-juli.jar
  server container: Apache Tomcat/8.0.14
  active sessions: total=1|/manager=1|/examples=0|/docs=0|/=0|/host-manager=0

JVM pid: 7262
  name: /usr/local/jboss-as-7.1.1.Final/jboss-modules.jar -mp /usr/local/jboss-as-7.1.1.Final/modules -jaxpmodule javax.xml.jaxp-provider org.jboss.as.standalone -Djboss.home.dir=/usr/local/jboss-as-7.1.1.Final
  thread count: 37 (peak: 82)
  class count: 6746
  heap memory: max=477364224|commited=69992448|used=35734024
  non-heap memory: max=369098752|commited=30900224|used=30841440
  java version: 1.7.0_45 (Oracle Corporation)
  java home: /usr/local/jdk1.7.0_45/jre
  jvm arguments: [-D[Standalone], -XX:+TieredCompilation, -Xms64m, -Xmx512m, -XX:MaxPermSize=256m, -Djava.net.preferIPv4Stack=true, -Dorg.jboss.resolver.warning=true, -Dsun.rmi.dgc.client.gcInterval=3600000, -Dsun.rmi.dgc.server.gcInterval=3600000, -Djboss.modules.system.pkgs=org.jboss.byteman, -Djava.awt.headless=true, -Djboss.server.default.config=standalone.xml, -Dorg.jboss.boot.log.file=/usr/local/jboss-as-7.1.1.Final/standalone/log/boot.log, -Dlogging.configuration=file:/usr/local/jboss-as-7.1.1.Final/standalone/configuration/logging.properties]
  class path: /usr/local/jboss-as-7.1.1.Final/jboss-modules.jar
  server container: JBoss Web/7.0.13.Final
  active sessions: total=0|/=0

JVM pid: 6464
  name: com.ca.directory.jxplorer.JXplorer
  thread count: 20 (peak: 20)
  class count: 3501
  heap memory: max=837287936|commited=24641536|used=7903016
  non-heap memory: max=-1|commited=31195136|used=30584736
  java version: 1.8.0_05 (Oracle Corporation)
  java home: /usr/local/jdk1.8.0_05/jre
  jvm arguments: [-Dfile.encoding=utf-8]
  class path: .:/usr/local/jxplorer/jars/junit.jar:/usr/local/jxplorer/jars/jxplorer.jar:/usr/local/jxplorer/jars/jhall.jar:/usr/local/jxplorer/jars/testprovider.jar:/usr/local/jxplorer/jars/jxworkbench.jar:/usr/local/jxplorer/jars/help.jar:/usr/local/jxplorer/jasper/lib/*


Changelog
======================================================================
2014-10-14 - Added sessions property
2014-10-03 - JvmInspector minor bugfixes
2014-09-11 - check_jvm bugfixes - shell and process UID
2014-09-04 - Initial release

