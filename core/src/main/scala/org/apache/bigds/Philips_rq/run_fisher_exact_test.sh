. conf/Philips_rq-Depolyment

${SPARK_HOME}/bin/spark-submit --class "com.intel.Philips.stat.FiExactTest" --master ${SPARK_MASTER} target/scala-2.10/philips-requirements_2.10-0.0.1.jar ${SPARK_MASTER} 
