package me.mile.kjb.activity;
import me.mile.kjb.model.TvDataBean;

/**
 * Created by Administrator on 2017/12/21.
 */

public interface ISearchResultActivity {

    void addToStore(String filename, TvDataBean tvDataBean);
    void deletStore(String filename);
}
