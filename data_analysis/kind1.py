# coding:utf-8

import os
import matplotlib.pyplot as plt
import sys

reload(sys)
sys.setdefaultencoding('utf8')

# # 创建文件夹
def mkdir(path):
    folder = os.path.exists(path)

    if not folder:  # 判断是否存在文件夹如果不存在则创建为文件夹
        os.makedirs(path)  # makedirs 创建文件时如果路径不存在会创建这个路径
        print "---  OK  ---"
    else:
        os.makedirs(path)
        print "---  There is this folder!  ---", path
#
for i in range(1,16):
    mkdir('F:/notebook/hw/code/sdk-python/data/pic/flavor_' + str(i))

f = open("F:/notebook/hw/code/sdk-python/data/data161.txt")
line = f.readline()

font1 = {'family' : 'Times New Roman',
'weight' : 'normal',
'size'   : 23,
}


kind = []
date = []

for i in range(1, 16):
    kind_choose = '%d' % i

    while line:
        a = line.split()
        temp = a[1].split('r')
        # print(a[2].split('-'))
        kind.append(temp[1])
        date.append(a[2])
        line = f.readline()

    nums = []
    temp = int(date[0].split('-')[2])
    i = 0
    nums.append(0)
    dates = []
    dates.append("01")
    # for i in (1,31):
    #     dates.append(i)
    j = 0
    month = date[0][:7]
    for each in date:
        end = int(each.split('-')[2])
        if end == temp:
            if kind[j] == kind_choose:
                nums[i] += 1
                j += 1
            else:
                j += 1
        else:
            if end - 1 != temp:
                for m in range(temp + 1, end):
                    nums.append(0)
                    if m < 10:
                        dates.append( '0' + str(m))
                    else:
                        dates.append(m)

            d = each.split('-')
            nums.append(0)
            # print each
            dates.append(d[2])
            i += 1
            if kind[j] == kind_choose:
                nums[i] += 1
                j += 1
            else:
                j += 1
            temp = end


    #画图
    f.close()
    plt.figure(figsize=(10,5))
    plt.plot(dates,nums)
    plt.grid(True) #增加格点
    plt.plot(nums, 'b', lw=1.5)  # 蓝色的线
    plt.plot(nums, 'ro')  # 离散的点
    plt.xlabel('dates')
    plt.ylabel('nums')
    plt.title(month + '-flavor' + kind_choose)

    plt.savefig('F:/notebook/hw/code/sdk-python/data/pic/flavor_' + kind_choose + '/' + month + '-flavor' + kind_choose + ".png")
    # # plt.show()
