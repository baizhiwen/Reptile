package com.lietu.dup;



public class SynonymReplace {


	/** The word list with score. */
	public static SynonymDic synonymDic = SynonymDic.getInstance();
	
	public static String replace(String content) throws Exception
	{
		if (content == null){
			return null;
		}
		int len = content.length();
		StringBuilder ret = new StringBuilder(len);
		SynonymDic.PrefixRet matchRet = new SynonymDic.PrefixRet(null,null);
		
		for(int i=0;i<len;)
		{
			//����Ƿ���ڴӵ�ǰλ�ÿ�ʼ��ͬ���
			synonymDic.checkPrefix(content,i,matchRet);
			if(matchRet.value == SynonymDic.Prefix.Match)
			{
				ret.append(matchRet.data);
				i=matchRet.next;//��һ��ƥ��λ��
			}
			else //����һ���ַ���ʼƥ��
			{
				ret.append(content.charAt(i));
				++i;
			}
		}
		
		return ret.toString();
	}
}
