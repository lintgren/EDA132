class Tree(object):
    "Generic tree node."
    def __init__(self, name='root', children=None):
        self.name = name
        self.nodes = []
        self.value =0
        self.children = []
        if children is not None:
            for child in children:
                self.add_child(child)
    def __repr__(self):
        return self.name
    def add_child(self, node):
        assert isinstance(node, Tree)
        self.children.append(node)
    def print(self):
        if(self.name == "leaf"):
            ''''''
        else:
            print(self.name)
            if self.children.__len__()>0:
                for child in self.children:
                    child.print()

    def __str__(self, level=0):
        # ret = ""
        # if(self.name.lower() != 'leaf'):
        ret = "\t" * level + repr(self.name) + "\n"
        for child in self.children:
            ret += child.__str__(level + 1)
        return ret