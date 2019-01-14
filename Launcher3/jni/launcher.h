#ifndef _APP_LAUNCHER_H
#define _APP_LAUNCHER_H

#include "elcommon.h"

#ifdef __cplusplus
extern "C" {
#endif

int launcher_addItem(ElItem * item) ;

void launcher_removeItem(int id);

int launcher_init();
int launcher_activate(const char * code) ;

ElList * launcher_list() ;

void launcher_setInstallerAllowed(Boolean allowed);
Boolean launcher_isAllowed(const char * pkg, const char * cls, Boolean banInstaller);


#ifdef __cplusplus
}
#endif

#endif
