#include <stdlib.h>
#include <jni.h>
#include  "stack_struct.h"


PSTACK pStack=0;

jboolean JNICALL Java_com_qiaoyf_appmemoryresearch_MainActivity_addNativeMem
(JNIEnv *env, jobject this, jint size)
{
    //申请头结点
    if(pStack==0)
    {
      pStack=(PSTACK)malloc(sizeof(STACK));
      Create_Stack(pStack);
    }
     char *nativeHeap=(char*)malloc(size*1024);
     if(nativeHeap == NULL)
       return false;

     Push_Stack(pStack,nativeHeap);
     return true;


}


jboolean JNICALL Java_com_qiaoyf_appmemoryresearch_MainActivity_removeNativeMem
(JNIEnv *env, jobject this, jint size)
{
   //释放头结点
   if(pStack!=0 && Is_Empty(pStack))
   {
     free(pStack->pTop);
     pStack->pTop=0;
     free(pStack);
     pStack=0;
   }
   if(0==pStack)
   {
    return false;
   }
   char *val=0;
   Pop_Stack(pStack,&val);
   free(val);
   return true;

}