#ifndef __STACK_H_
#define __STACK_H_
#include <stdbool.h>
typedef char* elementype;
//定义一个节点
typedef struct Node
{
    elementype data;
    struct Node *pNext;
}NODE,*PNODE;//
//构造一个栈
typedef struct stack_struct
{
    PNODE pTop; //栈顶指针
    PNODE pBottom;//栈底指针
}STACK,*PSTACK;
//函数声明区
void Create_Stack(PSTACK S);
bool Push_Stack(PSTACK S,elementype val);
bool Pop_Stack(PSTACK S,elementype *val);
bool Is_Empty(PSTACK pS);
void Traverse_Stack(PSTACK S);
void Clear_Stack(PSTACK S);
#endif