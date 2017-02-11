package pallettown;

/**
 * Created by Owner on 11/02/2017.
 */
public class MethodResult<T> {

    Exception Error;
    String Value;
    boolean Success;
    T data;

    public MethodResult(){}

    public MethodResult(T data){
        this.data = data;
    }

}

