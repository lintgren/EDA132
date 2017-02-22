import os
import regex as re
import math
import sys
from Tree import Tree
from operator import itemgetter
from scipy.stats import chi2

def get_attr(text, filename):
    dictWithAttr = dict()
    attrNameList = list()
    regForAttr = re.compile(r"(?<=@attribute )['a-z'|-]*")
    for itr_attr in regForAttr.finditer(text.lower()):
        attr = itr_attr.group()
        reg_values_part = re.compile("(?<="+attr+" ){[A-Z||a-z||\'||\,||$||0-9||\-||>||\ ]+}")
        dictWithAttr[attr] = list()
        attrNameList.append(attr)
        for values_part in reg_values_part.finditer(text.lower()):
            reg_value = re.compile(r"[a-z||$||0-9||\-||>]+")
            # reg_value = re.compile("[A-za-z]+")
            # reg_values = re.compile(r"[a-z]*")
            '''dictWithAttr[attr[1:-1]] = [None]*sum(1 for _ in reg_value.finditer(values_part.group()))'''
            for idx,value in enumerate(reg_value.finditer(values_part.group())):
                # print(attr)
                dictWithAttr[attr].append(value.group())
                '''dictWithAttr[attr[1:-1]][idx] = (value.group()[1:-1])'''
    return attrNameList,dictWithAttr

def get_data(text):
    re_data_field = re.compile("(?<=@data)[^.]*")
    x = list()
    y= list()
    for data_field in re_data_field.finditer(text.lower()):
        re_data_row = re.compile("[^\n||^%]+")
        for data_row in re_data_row.finditer(data_field.group()):
            re_data_value = re.compile("[^\,||^']+")
            dx = list()
            for data_value in re_data_value.finditer(data_row.group()):
                if data_value.group():
                    dx.append(data_value.group())
            if dx.__len__()>0:
                last = dx.pop(-1)
                y.append(last)
                x.append(dx)
    return x,y

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

def get_sum_y_value(y,y_value):
    total = 0
    for value in y:
        if value == y_value:
            total += 1
    return total

def split(x,y,column_index,x_value,y_value):
    '''
    How many rep or demo that answered x_value for the question at column_index
    :param x:
    :param y:
    :param column_index:
    :param x_value:
    :param y_value:
    :return:
    '''
    total = 0
    for idx,row in enumerate(x):
        if row[column_index] == x_value and y[idx] == y_value:
           total += 1
    return total

def get_sum_y_value_node(x,column_index,x_value):
    total = 0
    for idx, row in enumerate(x):
        if row[column_index] == x_value:
            total += 1
    return total

def update_data_set(x,y,column_index,x_value):
    dx = list()
    dy = list()
    for idx,row in enumerate(x):
        if row[column_index] == x_value:
            dx.append(row)
            dy.append(y[idx])
    return dx,dy


def calc_gain(x, y, column_index, x_value_list, y_value_list):
    class_amount = list()
    for y_value in y_value_list:
        class_amount.append(get_sum_y_value(y,y_value))
    # total_class_dist = y_value_list.__len__()
    total_class_dist = sum(class_amount)
    data_set_entropy = 0
    for class_value in class_amount:
        data_set_entropy -= (class_value/total_class_dist)*math.log2(class_value/total_class_dist)
    gain = data_set_entropy
    for x_value in x_value_list:
        node_entropy = 0
        total_sub_set = get_sum_y_value_node(x,column_index,x_value)
        for y_value in y_value_list:
            node_dist = split(x,y,column_index,x_value,y_value)
            if node_dist != 0:
                node_entropy -= (node_dist/total_sub_set)*math.log2(node_dist/total_sub_set)
        gain -= (total_sub_set/total_class_dist)*node_entropy
    # if x.__len__()== 2:
    #     print(str(gain) + "\t" +str(column_index))
    return round(gain,6)

def find_best_gain_index(x,y,attrs,attrNameList,class_values):
    best_gain_index = -1
    best_gain = -1
    for key, values in attrs.items():
        for index, attr in enumerate(attrNameList):
            if attr == key :
                '''
                attr == key för att hitta de olika values som vi får fram i attr.items() så det inte blir fel values.
                '''
                gain = calc_gain(x, y, index, values, class_values)
                if gain > best_gain:
                    best_gain = gain
                    best_gain_index = index
    return best_gain,best_gain_index

def is_pure_set(y):
    first_item =None
    if y.__len__() >0:
        first_item = y[0]
    else:
        return False, None
    for row in y:
        if row != first_item:
            return False,None
    return True,first_item

def rec(x,y,attrNameList,attrs,parent_examples,class_values):
    node = Tree()
    is_pure,first_item = is_pure_set(y)
    if(x.__len__()==0):
        class_amount = list()
        for y_value in class_values:
            class_amount.append((y_value,get_sum_y_value(parent_examples, y_value)))
        return Tree(max(class_amount,key=itemgetter(1)),class_amount)
    elif(is_pure):
        return Tree(first_item + " " + str(y.__len__()),[(first_item,y.__len__())])
    elif(attrNameList.__len__()==0):
        class_amount = list()
        for y_value in class_values:
            class_amount.append((y_value, get_sum_y_value(y, y_value)))
        return Tree(max(class_amount, key=itemgetter(1)),class_amount)
    else:
        best_gain,best_gain_index = find_best_gain_index(x,y,attrs,attrNameList,class_values)
        node.name = attrNameList[best_gain_index]
        adsf = list()
        for class_value in class_values:
            adsf.append((class_value,y.count(class_value)))
        node.values = adsf
        tmp_attrs = attrs.copy()
        x_values = tmp_attrs.pop(attrNameList[best_gain_index])
        for value in x_values:
            '''
            höger eller vänster
            '''
            dx = x.copy()
            dy = y.copy()
            ddx, ddy = update_data_set(dx, dy, best_gain_index, value)
            tmp_attrNameList = attrNameList[:]
            tmp_attrNameList[best_gain_index] = None
            node.add_child(rec(ddx,ddy,tmp_attrNameList,tmp_attrs,dy,class_values))
            # best_gain, best_gain_index = find_best_gain_index(ddx, ddy, tmp_attrs, tmp_attrNameList, class_values)
            # if best_gain == 1 or best_gain == 0:
            #     return Tree('leaf')
            # if best_gain_index > -1:
            #     node.add_child(rec(ddx, ddy,tmp_attrNameList,tmp_attrs,dx,class_values))
    return node


def prune(tree):
    if(tree.check_if_leaf()):
        return tree
    else:
        leaf_children = True
        for child in tree.children:
            if(not child.check_if_leaf()):
                leaf_children = False
                if prune(child).check_if_leaf():
                    leaf_children = True
        if leaf_children:
            class_dist = sum(j for i, j in tree.values)
            k = tree.values.__len__()
            m = tree.children.__len__()
            attr_pos_proportion = (tree.values[0][0],tree.values[0][1]/class_dist)
            attr_neg_proportion = (tree.values[1][0],tree.values[1][1]/class_dist)
            total_deviation = 0
            for child in tree.children:
                pknk = sum(j for i, j in child.values)
                for i,j in child.values:
                    pk = j
                    pk_hat =0
                    if attr_neg_proportion[0] == i:
                        pk_hat = attr_pos_proportion[1] * (pknk)
                    else:
                        pk_hat = attr_neg_proportion[1] * (pknk)
                    total_deviation += (math.pow(pk-pk_hat,2)/pk_hat)
            p_value = chi2.cdf(total_deviation, df=(m - 1) * (k - 1))
            if p_value>= 0.95:
                tree.children = []
                tree.values =[max(tree.values,key=itemgetter(1))]
                tree.name = max(tree.values,key=itemgetter(1))
                return tree
            else:
                return tree
    return tree

def main():
    test_set_name =sys.argv[1]
    data_set = test_set_name
    # data_set = "weather.nominal.arff"
    attrNameList,attrs = get_attr(open("data/" + data_set).read(), "hej")
    if(test_set_name=='vote.arff'):
        class_values = attrs.pop("'class'")
    else:
        class_values = attrs.pop("willwait")
    attrNameList.pop(attrNameList.__len__()-1)
    x,y= get_data(open("data/" + data_set).read())
    root_node = rec(x,y,attrNameList,attrs,x,class_values)
    print("unpruned tree:")
    print(root_node.__str__())
    prune(root_node)
    print("\n\n\n pruned tree:")
    print(root_node.__str__())

if __name__ == "__main__":
    main()