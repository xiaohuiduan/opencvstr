import cv2
import time
video = cv2.VideoCapture("视频地址")
# video = cv2.VideoCapture(0)
out = "@#&$%*o!; "
lenStr = len(out)
while(1):
    ret, frame = video.read()
    cv2.imshow("capture", frame)
    if not ret:
        break

    size = frame.shape
    frame = cv2.cvtColor(frame,cv2.COLOR_RGB2GRAY)
    result = ""
    row = size[0]
    col = size[1]
    for i in range(0,row,7):
        for j in range(0,col,7):
            gray = frame[i][j]
            index = int(round(gray*(lenStr+1)/255))
            result+= " " if index >= lenStr else out[index]
        result += "\n"
    print("\033c")
    print(result)
    
    if cv2.waitKey(100) & 0xFF == ord('q'):
        break

