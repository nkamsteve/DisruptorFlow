package ca.rdmss.test.dflow;

import java.util.concurrent.atomic.AtomicInteger;

import ca.rdmss.dflow.DisruptorFlow;
import ca.rdmss.dflow.ExceptionHandler;
import ca.rdmss.dflow.TaskAsync;
import ca.rdmss.dflow.TaskFlow;
import ca.rdmss.dflow.TaskSync;
import ca.rdmss.dflow.TaskTransition;
import ca.rdmss.dflow.impl.ContextEvent;

public class DFlowExample {

    static public void main(String[] args){
    	
	    DisruptorFlow<TestContext> dflow = new DisruptorFlow<TestContext>();

	    dflow.setExceptionHandler(new ExceptionHandler<ContextEvent<TestContext>>(){
	        @Override
	        public TaskTransition handleTaskException(ContextEvent<TestContext> context, Throwable ex) {
	            System.err.printf("Flow failed due to %s\n", ex.getMessage());
	            ex.printStackTrace();
	            return TaskTransition.Fail;
	        }
	    });

	    dflow.start();

	    TaskFlow<TestContext> flow = new TaskFlow<TestContext>(
	            new TestSyncTask(),
	            new TestAsyncTask(),
	            new TestSyncTask());


	    dflow.process(new TestContext(), flow);
	    
	    dflow.stop();
    }    





    static AtomicInteger tasskIdGenerator = new AtomicInteger(0);
	
    static class TestContext extends ContextEvent<TestContext>{
    }
	
    static class TestSyncTask extends TaskSync<TestContext> {
    	int id = tasskIdGenerator.incrementAndGet();
    	
        @Override
        public TaskTransition execute(TestContext context) throws Throwable {
            System.out.printf("%d) Hi from Sync!\n", id);
            return TaskTransition.Next;
        }
    }

    static public class TestAsyncTask extends TaskAsync<TestContext> {
    	int id = tasskIdGenerator.incrementAndGet();

    	@Override
        public TaskTransition execute(TestContext context) throws Throwable {
            System.out.printf("%d) Hi from Async!\n", id);
            return TaskTransition.Next;
        }
    }

}
