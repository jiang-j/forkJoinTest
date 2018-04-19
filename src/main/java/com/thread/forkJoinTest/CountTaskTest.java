package com.thread.forkJoinTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * Created by jiangjian on 2018/4/19.
 */
public class CountTaskTest extends RecursiveTask<List<Integer>> {

    private static final int THREAD_HOLD = 2;

    private int start;
    private int end;

    public CountTaskTest(int start, int end){
        this.start = start;
        this.end = end;
    }

    @Override
    protected List<Integer> compute() {
        List<Integer> list = new ArrayList<>();
        int sum = 0;
        //如果任务足够小就计算
        boolean canCompute = (end - start) <= THREAD_HOLD;
        if(canCompute){
            for(int i=start;i<=end;i++){
                sum += i;
                list.add(i);
            }
        }else{
            int middle = (start + end) / 2;
            CountTaskTest left = new CountTaskTest(start,middle);
            CountTaskTest right = new CountTaskTest(middle+1,end);
            //执行子任务
            left.fork();
            right.fork();
            //获取子任务结果
            List<Integer> lResult = left.join();
            List<Integer> rResult = right.join();
            list.addAll(lResult);
            list.addAll(rResult);
        }
        return list;
    }

    public static void main(String[] args){
        ForkJoinPool pool = new ForkJoinPool();
        CountTaskTest task = new CountTaskTest(1,6);
        Future<List<Integer>> result = pool.submit(task);
        try {
            System.out.println(result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
