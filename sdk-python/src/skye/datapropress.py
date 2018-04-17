# -*- coding: utf-8 -*-

# @Author  : Skye
# @Time    : 2018/4/17 21:11
# @desc    :

def read_lines(file_path):
    if os.path.exists(file_path):
        array = []
        with open(file_path, 'r') as lines:
            for line in lines:
                array.append(line)
        return array
    else:
        print 'file not exist: ' + file_path
        return None


def main():
    path = 'D:\Works\competition\huawei\second\data\testCase'
    train_path =  path + 'TrainData_2015.12.txt'
    test_path = 'TestData_2016.1.8_2016.1.14.txt'


    train_data_arr = read_lines(train_path)
    test_data_arr = read_lines(test_path)


if __name__ == "__main__":
    main()
