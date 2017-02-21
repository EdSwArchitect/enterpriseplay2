package com.ekb.akka.parse;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ebrown on 2/16/2017.
 */
public class WebProxyParseTest {
    final private String WB = "2017-02-16 18:12:28     reason=Allowed  event_id=6387770633353691380    " +
    "protocol=HTTPS  action=Allowed  transactionsize=9951    responsesize=9430       requestsize=521     "+
            "urlcategory=Corporate Marketing serverip=127.101.32.207 clienttranstime=5       "+
            "requestmethod=get       refererURL=erp-sas.icims.com/jobs/search?in_iframe=1&amp;"+
            "hashed=607117230&in_iframe=1&mobile=false&width=1108&height=500&bga=true&needsRedirect=false&jan"+
            "1offset=-300&jun1offset=-240       useragent=Google Chrome (56.x)      product=NSS     "+
            "location=Port 10066     ClientIP=127.173.8.5    status=200 - OK user=some.user@foo.acme.com     "+
            "url=js-agent.mygumbo.acme.com/nr-1016.min.js    vendor=Zscaler  hostname=js-agent.mygumbo.acme.com  "+
            "clientpublicIP=127.173.8.5      threatcategory=Clean Transaction        threatname=None     "+
            "filetype=GZIP   appname=generalbrowsing pagerisk=0      "+
            "department=Framework Testing For Business Intelligence Dept     urlsupercategory=Business and Economy       "+
            "appclass=General Browsing       dlpengine=None  urlclass=Business Use   threatclass=Clean Transaction   "+
            "dlpdictionaries=None    fileclass=Archive Files bwthrottle=NO   servertranstime=5";

    @Test

    public void testParsing() {

        //useragent=(.*)\+product=(\w+)(.*)

//        Pattern pat = Pattern.compile("(\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+reason=(\\w+)\\s+event_id=(\\d+)\\s+protocol=(\\w+)\\s+action=(\\w+)\\s+transactionsize=(\\d+)" +
//                "\\s+responsesize=(\\d+)\\s+requestsize=(\\d+)\\s+urlcategory=(.+)\\s+serverip=([\\d\\.]+)\\s" +
//                "clienttranstime=(\\d+)\\s+requestmethod=(\\w+)\\s+refererURL=(.+)useragent=(.*)product=(\\w+)\\s+" +
//                "location=(.+)ClientIP=([\\d\\.]+)\\s+status=(.+)user=(.+)url=(.+)vendor=([\\w\\d]+)" +
//                "\\s+hostname=(.+)clientpublicIP=([\\d\\.]+)\\s+threatcategory=(.+)threatname=(\\w+)\\s+" +
//                "filetype=([\\w\\d]+)\\s+appname=(.+)pagerisk=(.+)department=(.+)urlsupercategory=(.+)" +
//                "appclass=(.+)dlpengine=(.+)urlclass=(.+)threatclass=(.+)dlpdictionaries=(.+)fileclass=(.+)" +
//                "bwthrottle=(.+)servertranstime=(.*)");

        Pattern pat = Pattern.compile("(\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+reason=(.+)event_id=(.+)protocol=(.+)action=(.+)transactionsize=(.+)" +
                "responsesize=(.+)requestsize=(.+)urlcategory=(.+)\\s+serverip=(.+)" +
                "clienttranstime=(.+)requestmethod=(.+)refererURL=(.+)useragent=(.*)product=(.+)" +
                "location=(.+)ClientIP=([\\d\\.]+)\\s+status=(.+)user=(.+)url=(.+)vendor=(.+)" +
                "hostname=(.+)clientpublicIP=([\\d\\.]+)\\s+threatcategory=(.+)threatname=(.+)" +
                "filetype=(.+)appname=(.+)pagerisk=(.+)department=(.+)urlsupercategory=(.+)" +
                "appclass=(.+)dlpengine=(.+)urlclass=(.+)threatclass=(.+)dlpdictionaries=(.+)fileclass=(.+)" +
                "bwthrottle=(.+)servertranstime=(.*)");

        //"bwthrottle=(\\w+)\\sservertranstime=(.*)");

        HashMap<String, String>bits = new HashMap<String, String>();

        System.out.format("'%s'%n", WB);

        Matcher matcher = pat.matcher(WB);

        if (matcher.matches()) {
            bits.put("date", matcher.group(1).trim());
            bits.put("reason", matcher.group(2).trim());
            bits.put("protocol", matcher.group(4).trim());
            bits.put("action", matcher.group(5).trim());
            bits.put("transactionsize", matcher.group(6).trim());
            bits.put("responsesize", matcher.group(7).trim());
            bits.put("requestsize", matcher.group(8).trim());
            bits.put("urlcategory", matcher.group(9).trim());
            bits.put("serverip", matcher.group(10).trim());
            bits.put("clienttranstime", matcher.group(11).trim());
            bits.put("requestmethod", matcher.group(12).trim());
            bits.put("refererURL", matcher.group(13).trim());
            bits.put("useragent", matcher.group(14).trim());
            bits.put("ClientIP", matcher.group(17).trim());
            bits.put("status", matcher.group(18).trim());
            bits.put("user", matcher.group(19).trim());
            bits.put("vendor", matcher.group(20).trim());
            bits.put("hostname", matcher.group(21).trim());
            bits.put("clientpublicIP", matcher.group(22).trim());
            bits.put("threatcategory", matcher.group(23).trim());
            bits.put("threatname", matcher.group(24).trim());
            bits.put("filetype", matcher.group(25).trim());
            bits.put("appname", matcher.group(26).trim());
            bits.put("department", matcher.group(27).trim());
            bits.put("pagerisk", matcher.group(28).trim());
            bits.put("urlsupercategory", matcher.group(29).trim());
            bits.put("appclass", matcher.group(30).trim());
            bits.put("urlclass", matcher.group(31).trim());
            bits.put("threatclass", matcher.group(32).trim());
            bits.put("dlpdictionaries", matcher.group(33).trim());
            bits.put("fileclass", matcher.group(34).trim());
            bits.put("bwthrottle", matcher.group(35).trim());
            bits.put("servertranstime", matcher.group(36).trim());

            int matches = matcher.groupCount();

            System.out.println("Match count: " + matches);

            for (int i = 1; i <= matches; i++) {
                System.out.println("\t" + i + ": - '" + matcher.group(i) + "'");
            }

            System.out.println("\n\n"+ bits);
        }
        else {
            Assert.fail("Parsing failed.");
        }

    }
}
