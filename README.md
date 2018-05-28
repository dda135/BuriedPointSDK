# 埋点SDK
一个简易的埋点SDK，埋点的作用是用于分析，类似于友盟之类的第三方服务
## 功能
（1）自定义计数事件<br>
（2）用户相关数据统计<br>
（3）页面停留时长和访问页面路径统计<br>
大概说一下设计思路，实际上就是分几个步骤<br>
1.上传数据：实际上就是把一系列的数据通过json格式上传上去，这里从数据量上面考虑，最终还是用了Protocol Buffer来传输<br>
2.数据采集：数据采集应该提供两种方式，第一种就是代码中打点处理，第二种就是预定义一些行为，比方说通过ActivityLifeCycle能够知道Activity的启动、进入后台以及活跃时长这些数据，然后自动上报<br>
3.数据存储：数据采集完成之后需要通过一定的方式进行存储，然后再上传到服务器上面，为了避免杀进程和上传频繁等问题，一般本地会用过SQLite或者一些存储方式进行数据存储，然后在到达一定量的时候去一起上传<br>
4.上传时机：数据采集和存储完成之后，应该在一些指定的时机上传，比方说存储数据量大于一定程度、启动App和App进入后台等等<br>
5.注解：这里定义了两个注解，方便做这样的两件事情，在页面展示和页面进入后台的时候可以上报自定义数据，这样做的意义是能够知道当前页面的含义，比方说一个商品详情页，通过添加这些参数，能够知道当前是什么商品，用户的跳出率怎样等等<br>
## 例子
首先是全局配置，这里实际上就是一些简单配置，比方说服务器接口地址、进入后台的时候数据是否上传等等
```
public class MainApplication extends Application{    

    @Override
    public void onCreate() {
        BuriedPointClient.getInstance().
                          attachApplication(this).
                          setConfiguration(new Configuration.Builder().
                          serverUrl("xxx").
                          shouldReportWhenBackground(true).
                          useIndex(false).
                          build());
    }                        
}
```
一些特定事件的上报，比方说登录、注册和注销
```
//登录，此时应该关联用户id
BuriedPointClient.getInstance().login("用户id");
//注册，此时应该关联用户id，当前事件和登录分开其实可以独立统计出当天注册人数
BuriedPointClient.getInstance().register("用户id");
//注销，后续事件不会再和之前关联的用户相关
BuriedPointClient.getInstance().loginOut();
```
页面展示和进入后台添加自定义数据，这两个数据是SDK内部自动采集的，但是为了扩展统计效果，需要手动允许添加数据
```
    //当前注解及返回Map<String,String>可以标记为添加页面展示数据
    @PageShow
    public Map<String,String> report(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("商品id","xxx");
        ...
        return map;
    }
    //当前注解及返回Map<String,String>可以标记为添加页面进入后台数据
    @PageBackground
    public Map<String,String> background(){
        HashMap<String,String> params = new HashMap<>();
        map.put("商品id","xxx");
        ...
        return params;
    }
```
