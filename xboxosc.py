import XboxController as xb
import sys
import time
import liblo

target = liblo.Address(57120)

if __name__ == '__main__':

    def controlCallBack(xboxControlId, value):
        l = xboxCont.controlValues.values()
        myv = l[0:len(l)-1]+[l[len(l)-1][0],l[len(l)-1][1]]
        liblo.send(target, "/xbox", *myv)
        print "%s" % myv

    xboxCont = xb.XboxController(controlCallBack, deadzone = 30, scale = 100, invertYAxis = True)

    try:
        xboxCont.start()
        while True:
            time.sleep(1)

    except KeyboardInterrupt:
        print "User cancelled"
    
    except:
        print "Unexpected error:", sys.exc_info()[0]
        raise
        
    finally:
        xboxCont.stop()
