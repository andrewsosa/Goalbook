//
//  CustomListCellTableViewCell.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 4/17/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit

class CustomListCellTableViewCell: UITableViewCell {

    @IBOutlet weak var listImage: UIImageView!
    
    @IBOutlet weak var listName: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
