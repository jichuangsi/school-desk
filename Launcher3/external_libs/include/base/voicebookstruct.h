#ifndef VOICEBOOK_STRUCT_H
#define VOICEBOOK_STRUCT_H


typedef struct _Textbook
{
	char  BookName[64] ;
	int     BookCode ;
	int    Opening ;
	int 	StartingPage;
	int 	PageTotal;
	int 	StartingPosition;
	int    OffsetX ;
	int    OffsetY ;
	int    PageWidth ;
	int    PageHeight ;
	int    PicWidth ;
	int    PicHeight ;
	float  Scale ;
}TextBook;


typedef struct _Test
{
	int 	blockId ;//default value: -1 ;
	int 	blockIndex ;//default value: -1 ;
}Test;

typedef struct _Info
{
	char  Pic[64];
	char  Show1[128];
	char 	Show2[128];
	int 	Voice1;
	int 	Voice2;
	int 	PageNo;
	int 	PageMode;
	int 	BlockTotal;
	int 	PagePosition;   //0为左边 1为右边
	int 	OffsetX;
	int 	OffsetY;
}Info;

typedef struct _Block
{
	int PageId ; // 20140323
	char 	Name[16];
	int  	Id;
	int  	Top;
	int  	Left;
	int  	Width;
	int  	Height;
	int  	Visible;
	char 	*Text1;
	char 	*Text2;
	char 	*Text3;
	int 	File1;
	int 	File2;
	int 	File3;
	int   	Quiz;

}Block;

typedef struct _Page
{
	int id ;  // 20140323
	int position ; // 20140323
	int blockTotal;
	Info info;
	Block block[120];
	Test test[4];
	int bufferSize ;
	int bufferOffset ;
	char * buffer ;
}Page;


#endif // MYSTRUCT_H
