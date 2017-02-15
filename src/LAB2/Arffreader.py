import os
import regex as re
import Tree

def get_attr(text, filename):
    dictWithAttr = dict()
    attrNameList = list()
    regForAttr = re.compile(r"(?<=@attribute )['a-z'|-]*")
    for itr_attr in regForAttr.finditer(text.lower()):
        attr = itr_attr.group()
        reg_values_part = re.compile(r"(?<="+attr+" ){[a-z||\'||\ ||\,]*}")
        dictWithAttr[attr[1:-1]] = list()
        attrNameList.append(attr[1:-1])
        for values_part in reg_values_part.finditer(text.lower()):
            reg_value = re.compile(r"'[a-z]*'")
            '''dictWithAttr[attr[1:-1]] = [None]*sum(1 for _ in reg_value.finditer(values_part.group()))'''
            for idx,value in enumerate(reg_value.finditer(values_part.group())):
                dictWithAttr[attr[1:-1]].append(value.group()[1:-1])
                '''dictWithAttr[attr[1:-1]][idx] = (value.group()[1:-1])'''
    return attrNameList,dictWithAttr

def get_data(text):
    regForAttr = re.compile(r"(?<=@attribute )['a-z'|-]*")
    for itr_attr in regForAttr.finditer(text.lower()):
        print(itr_attr)

    return 0

def get_files(dir, suffix):
    """
    Returns all the files in a folder ending with suffix
    :param dir:
    :param suffix:
    :return: the list of file names
    """
    files = []
    for file in os.listdir(dir):
        if file.endswith(suffix):
            files.append(file)
    return files


'''fileNames = get_files(fileName,"arff")'''
'''for filename in fileNames:'''
attrNameList,attrs = get_attr(open("data/" + "vote.arff").read(), "hej")
data_list = get_data(open("data/" + "vote.arff").read(),attrNameList.count())
for key, value in attrs.items():
    print(key)
    for m in value:
        print(m)
'''t = Tree('*', [Tree('1'),
               Tree('2'),
               Tree('+', [Tree('3'),
                          Tree('4')])])'''
'''filename[:-4])'''


