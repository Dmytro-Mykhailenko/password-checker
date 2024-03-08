package utils;

import io.restassured.response.Response;

import static io.restassured.RestAssured.get;

public class GetAllOrdersPerformanceTest implements Runnable {

    Thread thisThread;
    static String threadThatStoppedTest;
    static double load;
    static int timer;
    static int startTime;
    static int caughtCode;
    static volatile boolean stop;
    double threadCurrentLoad;
    int threadStatusCode;
    int requestsCount;
    int sumRespT;
    int respTime;
    int minResponseTime;
    int maxResponseTime;
    int time;
    int thrdTimer;

    GetAllOrdersPerformanceTest(String name) {

        thisThread = new Thread(this, name);

    }

    static void clear(GetAllOrdersPerformanceTest[] tth) {

        for (GetAllOrdersPerformanceTest testThread : tth) {

            if (testThread.thisThread.isAlive()) testThread.thisThread.interrupt();

        }

        GetAllOrdersPerformanceTest.threadThatStoppedTest = "";

    }

    public static int test(int threadsAmount, int load, int timer) throws InterruptedException {

        GetAllOrdersPerformanceTest.startTime = GetAllOrdersPerformanceTest.timer = timer * 1000;
        GetAllOrdersPerformanceTest.load = (load * 0.001) / threadsAmount;
        GetAllOrdersPerformanceTest.caughtCode = 0;
        GetAllOrdersPerformanceTest.stop = false;

        GetAllOrdersPerformanceTest[] thrd = new GetAllOrdersPerformanceTest[threadsAmount];

        for (int i = 0; i < thrd.length; i++) {

            thrd[i] = GetAllOrdersPerformanceTest.createAndStart("Thread-" + i);

        }

        while (!GetAllOrdersPerformanceTest.stop && GetAllOrdersPerformanceTest.timer > 0) {

            Thread.sleep(1000);
            GetAllOrdersPerformanceTest.timer -= 1000;

            double minRespT = 0;
            double maxRespT = 0;
            double sumOfAvgRespT = 0;
            int totalRequestsCount = 0;
            double minLoad = 0;
            double maxLoad = 0;
            int isAlive = 0;

            for (GetAllOrdersPerformanceTest performanceTest : thrd) {

                if (performanceTest.thisThread.isAlive()) isAlive++;

                if (minRespT == 0 || minRespT > performanceTest.minResponseTime)
                    minRespT = performanceTest.minResponseTime;
                if (maxRespT == 0 || maxRespT < performanceTest.maxResponseTime)
                    maxRespT = performanceTest.maxResponseTime;

                totalRequestsCount += performanceTest.requestsCount;
                sumOfAvgRespT += (double) performanceTest.sumRespT / performanceTest.requestsCount;

                if (maxLoad == 0 || maxLoad < performanceTest.threadCurrentLoad)
                    maxLoad = performanceTest.threadCurrentLoad;

                if (minLoad == 0 || minLoad > performanceTest.threadCurrentLoad)
                    minLoad = performanceTest.threadCurrentLoad;

            }

            if (isAlive == 0) {

                System.out.println("All threads are interrupted!");
                GetAllOrdersPerformanceTest.stop = true;

            }

            double i = timer - GetAllOrdersPerformanceTest.timer * 0.001;
            double s = (double) totalRequestsCount / thrd.length;
            double avgRespT = sumOfAvgRespT / thrd.length;

            System.out.println("\u21cb\nTotal avg. req/sec:\t\t\t\t\t\t\t\t  " + String.format("%.2f", totalRequestsCount / i) +
                    "\nReq/sec from each thread(" + isAlive + ") min-avg-max: " + String.format("%.3f", minLoad * 1000) +
                    " - " + String.format("%.3f", s / i) + " - " + String.format("%.3f", maxLoad * 1000) +
                    "\nResp. time min-avg-max:\t\t\t\t\t  " + String.format("%.3f", minRespT * 0.001) +
                    " - " + String.format("%.3f", avgRespT * 0.001) + " - " + String.format("%.3f", maxRespT * 0.001) +
                    "\nTime rem.: \t\t\t\t\t\t\t\t\t\t\t" + GetAllOrdersPerformanceTest.timer / 1000);

        }

        Thread.sleep(5000);
        GetAllOrdersPerformanceTest.clear(thrd);

        return GetAllOrdersPerformanceTest.caughtCode;

    }

    public static GetAllOrdersPerformanceTest createAndStart(String name) {

        GetAllOrdersPerformanceTest myThrd = new GetAllOrdersPerformanceTest(name);
        myThrd.thisThread.start();

        return myThrd;

    }

    @Override
    public void run() {

        thrdTimer = time = timer;

        while (caughtCode != 429 && timer != 0 && !stop) {

            if (thrdTimer != timer) thrdTimer = time = timer;

            threadCurrentLoad = (double) requestsCount / (startTime - time);

            if (threadCurrentLoad <= load) {

                Response response = get("http://35.208.34.242:8080/test-orders/get_orders")
                        .then()
                        .extract()
                        .response();

                requestsCount++;
                threadStatusCode = response.statusCode();
                respTime = (int) response.getTime();
                sumRespT += respTime;
                time -= respTime;

                if (minResponseTime == 0 || minResponseTime > respTime) minResponseTime = respTime;

                if (maxResponseTime == 0 || maxResponseTime < respTime) maxResponseTime = respTime;

                if (stop) return;

                else if (threadStatusCode == 429) {

                    stop = true;
                    caughtCode = threadStatusCode;
                    threadThatStoppedTest = thisThread.getName();
                    System.out.println("\n================================\n" + threadThatStoppedTest +
                            " stopped the test!" + "\n================================");
                    return;

                } else caughtCode = threadStatusCode;

            } else {

                time = time - 400;

                try {
                    System.out.print('.');
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
