//
//  CustomCell.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/19/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit

class CustomCell: UITableViewCell {

    @IBOutlet weak var mainLabel: UILabel!
    
    @IBOutlet weak var taskDate: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setCell(taskName: String){
    
        self.mainLabel.text = taskName
    
    }

}
