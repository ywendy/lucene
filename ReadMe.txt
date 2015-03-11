Field.Store.YES或者NO(存储域选项)
设置为YES表示把这个域中的内容完全存储到文件中，方便进行文本还原
设置NO表示把这个域中的

Field.Index(索引域选项)
Field.Index.ANALYZED：进行分词和索引，适用于标题和内容等。
Field.Index.NOT_ANALYZED 只进行索引，不分词如：id，身份证，姓名等，适用于精度搜索
Field.Index.ANALYZED_NO_NORMS：进行分词但是不存储norms信息，norms中包含了创建索引的时间和权值等信息
							  (norms主要存储的是加权信息,表示文档很重要)
Field.Index.NOT_ANALYZED_NO_NORMS:不分词也不存储norms信息
Field.Index.NO:不进行索引


BooleanQuery
Occur.MUST  必须出现
Occur.SHOULD 表示可以出现
Occur.MUST_NOT 必须不出现

