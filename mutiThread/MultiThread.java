import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

//多线程编程
public class MultiThread {
    public static void main(String args[]) throws Exception {
        System.out.println("我是主线程!");

        double threadsCount = 30;
        double total;
        double limit;


//        ArrayList<String> rows=new ArrayList<String>();
//        rows=getTxIds();
//        System.out.println(rows);
        ArrayList<String> a = new ArrayList<String>();
        File file = new File("/junping/event_2016-06-23.log");
        System.out.println(file.length());
        try {
            BufferedReader reader = null;
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            System.out.println("line:" + line);
            while ((line = reader.readLine()) != null) {
                a.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException:" + e.getMessage());
        }

        //下面创建线程实例thread1

        //创建thread2时以实现了Runnable接口的THhreadUseRunnable类实例为参数


        total = a.size();
//        System.out.println("remove:" + a.remove(0));
        System.out.println("total:" + total);
        System.out.println("threadsCount:" + threadsCount);
        limit = Math.ceil(total / threadsCount);
        System.out.println("limit:" + limit);
        ArrayList<String> tmp = new ArrayList<String>();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        int count = 0;

        for (int i = 0; i < total; i++) {
            if (count <= limit - 1) {
                tmp.add(a.get(i));
                count++;
            } else {
                count = 0;
                System.out.println("set up first thread");
                Thread newThead = new Thread(new ThreadUseRunnable(tmp));
                newThead.start();
                tmp = new ArrayList<String>();
                tmp.add(a.get(i));
            }
        }

        if (tmp.size() > 0) {
            System.out.println("has left");
            System.out.println("set up second thread");
            Thread newThead = new Thread(new ThreadUseRunnable(tmp));
            newThead.start();
        } else {
            System.out.println("no left");
        }
//        Thread thread2 = new Thread(new ThreadUseRunnable(a));

        //thread1.setPriority(6);//设置thread1的优先级为6
        //优先级将决定cpu空出时，处于就绪状态的线程谁先_PAIORITY
        //新线程继承创建她的父线程优先级,父线程通常有普通占领cpu开始运行
        //优先级范围1到10,MIN_PRIORITY,MAX_PRIORITY,NORM优先级即5NORM_PRIORITY
//        thread2.start();//启动thread2


    }//main

//    public static ArrayList<String> getTxIds() {
//        ArrayList<String> list = new ArrayList<String>();
//        String filePath = "/junping/event_2016-06-23.log";
//        File file = new File(filePath);
//        System.out.println(file.length());
//        BufferedReader reader = null;
//        reader = new BufferedReader(new FileReader(file));
//        String row = null;
//        while ((row = reader.readLine())!=null){
//            list.add(row);
//        }
//        return list;
//    }
}//MultiThread


class ThreadUseRunnable implements Runnable
//通过实现Runnable接口中的run()方法,再以这个实现了run()方法的类
//为参数创建Thread的线程实例
{

    ArrayList<String> tx_ids;

    //Thread thread2=new Thread(this);
    //以这个实现了Runnable接口中run()方法的类为参数创建Thread类的线程实例
    ThreadUseRunnable(ArrayList<String> a) {
        this.tx_ids = a;
    }//构造函数

    public void run() {
        System.out.println("我是Thread类的线程实例并以实现了Runnable接口的类为参数!");
//        System.out.println("我将挂起1秒!");
//        System.out.println(this.tx_ids);
//        System.out.println("回到主线程,请稍等,刚才主线程挂起可能还没醒过来！");
        try {
            String tx = null;
            while ((tx = this.tx_ids.remove(0)) != null) {
                decode(tx);
            }
        } catch (Exception e) {

        }
        //如果该run()方法顺序执行完了,线程将自动结束,而不会被主线程杀掉
        //但如果休眠时间过长,则线程还存活,可能被stop()杀掉
    }

    public void decode(String tx) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet("http://172.30.10.147:8088/peek/decode?transaction_id=" + tx);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try {
//                System.out.println(response1.getStatusLine());
                HttpEntity entity = response1.getEntity();
                if (entity != null) {
                    InputStream instreams = entity.getContent();
                    String str = convertStreamToString(instreams);
                    System.out.println(tx+"->"+str);
                    // do something useful with the response body
                    // and ensure it is fully consumed
                }
                EntityUtils.consume(entity);
            } finally {
                response1.close();
            }

        } finally {
            httpclient.close();
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
