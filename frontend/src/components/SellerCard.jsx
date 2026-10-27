import { Link } from 'react-router-dom';
import { ROUTES } from '../constants/routes';
import { formatDate, getFullName } from '../utils/format';
import Avatar from './Avatar';
import RatingBadge from './RatingBadge';

export default function SellerCard({ seller, position }) {
  const fullName = getFullName(seller);

  return (
    <Link to={ROUTES.seller(seller.id)} className="seller-card">
      <span className="seller-card__position">{position}</span>
      <Avatar name={fullName} />
      <div className="seller-card__body">
        <h3 className="seller-card__name">{fullName}</h3>
        <p className="seller-card__meta">Member since {formatDate(seller.registrationDate)}</p>
      </div>
      <RatingBadge value={seller.rating} />
    </Link>
  );
}
