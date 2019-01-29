#ifndef PLAN_H
#define PLAN_H

#include "elcommon.h"
#define MAXDAYS (400)
#define FINISHED     (0x01)
#define UNFINISHED   (0x00)
#define DELLED  (0x10)
#define OVERLAY     (0)
#define DELAY       (1)

//单元结构
typedef struct _Unit
{
    int day;
    int unitNo;
    int state;   //为“NONOTFINISHED表示还未复习，为”FINISHED"时表示已经复习
    int repetition;
    int advance; //为均衡安排，有时把第二天的提前到今天复习。preDay记录某单元提前的天数
    struct _Unit *next;
}Unit;

typedef struct _DailyPlan
{
    int day;
    int total ; //总单元数
    int left ; //未完成的单元数（不区分repitition）
    int reviewLeft; //未完成复习的单元数
    Unit *unitPtr;
}DailyPlan;








#endif // SCHEDUAL_H
