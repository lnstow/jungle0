# jungle0
为一个网站制作的安卓app，主要使用的技术如下  

- 用户界面使用了recyclerview和flexbox布局，还有谷歌的material库  
- 网络请求使用okhttp并使用拦截器处理缓存控制  
- 请求的结果通过jsoup解析成一个文档，并通过dom操作获取需要的数据  
- cookie持久化使用gson转换字符串保存到文件  
- 解析的结果通过recyclerview展示出来，使用glide加载图片  

- 整个应用是单activity多fragment组成的，所有页面如列表页和详情页都是使用fragment加载，在应用中手动维护一个fragment栈  
- 实现了滑动返回的功能，在滑动时半透明显示下层fragment。fragment间通过activity传递数据  
- 消息弹框使用dialogFragment实现，并且缩短了dialog的宽度，让内容在铺满屏幕时，仍然可以点到旁边的空白区域来取消对话框  

- 数据库存储使用room框架，记录了应用的浏览记录和用户的收藏，侧滑打开navigationView的界面  
- 使用TabLayout+ViewPager展示用户的浏览记录、稍后阅读、当前浏览栈（即fragment栈），左右滑动在三个tab中切换
